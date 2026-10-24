package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tcms_school_transport")
public class SchoolTransportEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Boolean transportAvailable;
  public Integer numberOfBuses;
  public Boolean gpsEnabled;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "transport_id")
  public java.util.List<SchoolDriverEntity> drivers;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "transport_id")
  public java.util.List<SchoolRouteEntity> routes;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "transport_id")
  public java.util.List<SchoolVehicleEntity> vehicles;
}
