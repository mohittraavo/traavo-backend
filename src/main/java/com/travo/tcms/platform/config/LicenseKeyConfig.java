package com.travo.tcms.platform.config;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LicenseKeyConfig {

    @Bean
    public PrivateKey licenseSigningKey() {
        try {
            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            KeyPair keyPair =
                    generator.generateKeyPair();

            return keyPair.getPrivate();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to generate license signing key",
                    e
            );
        }
    }
}