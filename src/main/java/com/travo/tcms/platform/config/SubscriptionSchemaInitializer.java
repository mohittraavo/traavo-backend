package com.travo.tcms.platform.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class SubscriptionSchemaInitializer {
  private static final Logger log = LoggerFactory.getLogger(SubscriptionSchemaInitializer.class);

  private final JdbcTemplate jdbcTemplate;
  private final DataSource dataSource;

  public SubscriptionSchemaInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
    this.jdbcTemplate = jdbcTemplate;
    this.dataSource = dataSource;
  }

  @PostConstruct
  void ensureSubscriptionColumns() {
    if (!isPostgreSql()) {
      log.info("Skipping Postgres-only subscription column migration on {}", databaseProductName());
      return;
    }

    jdbcTemplate.execute("ALTER TABLE tcms_schools ADD COLUMN IF NOT EXISTS subscription_grace_period_days integer NOT NULL DEFAULT 0");
    jdbcTemplate.execute("ALTER TABLE tcms_schools ADD COLUMN IF NOT EXISTS subscription_grace_period_end_date date");
    jdbcTemplate.execute("ALTER TABLE tcms_subscriptions ADD COLUMN IF NOT EXISTS grace_period_days integer NOT NULL DEFAULT 0");
    jdbcTemplate.execute("ALTER TABLE tcms_subscriptions ADD COLUMN IF NOT EXISTS grace_period_end_date date");

    ensureTextColumn("tcms_subscription_plans", "modules_json");
    ensureTextColumn("tcms_subscription_plans", "features_json");
    ensureTextColumn("tcms_subscription_plans", "limits_json");
    ensureTextColumn("tcms_subscriptions", "modules_json");
    ensureTextColumn("tcms_subscriptions", "features_json");
    ensureTextColumn("tcms_subscriptions", "usage_json");
    ensureTextColumn("tcms_subscription_audit", "old_value");
    ensureTextColumn("tcms_subscription_audit", "new_value");
  }

  private void ensureTextColumn(String tableName, String columnName) {
    Integer oidColumnCount = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'public' AND table_name = ? AND column_name = ? AND udt_name = 'oid'",
        Integer.class, tableName, columnName);
    if (oidColumnCount != null && oidColumnCount > 0) {
      jdbcTemplate.execute(
          "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE TEXT USING "
              + "CASE WHEN " + columnName + " IS NULL THEN NULL ELSE convert_from(lo_get(" + columnName + "), 'UTF8') END");
    }
  }

  private boolean isPostgreSql() {
    return databaseProductName().toLowerCase().contains("postgresql");
  }

  private String databaseProductName() {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();
      String name = metaData.getDatabaseProductName();
      return name == null ? "" : name;
    } catch (SQLException exception) {
      log.warn("Could not detect database product name", exception);
      return "";
    }
  }
}
