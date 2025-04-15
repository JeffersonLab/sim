package org.jlab.sim.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.sim.persistence.enumeration.SoftwareType;

@Entity
@Table(name = "SOFTWARE", schema = "SIM_OWNER")
public class Software implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SoftwareId", sequenceName = "SOFTWARE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SoftwareId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SOFTWARE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger softwareId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NAME", nullable = false, length = 128)
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "TYPE", nullable = false, length = 64)
  @Enumerated(EnumType.STRING)
  private SoftwareType type;

  @Size(max = 256)
  @Column(name = "DESCRIPTION", nullable = true, length = 256)
  private String description;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "REPOSITORY_ID", referencedColumnName = "REPOSITORY_ID", nullable = false)
  private Repository repository;

  @Size(max = 256)
  @Column(name = "HOME_URL", nullable = true, length = 256)
  private String homeUrl;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "MAINTAINER_USERNAME_CSV", nullable = false, length = 256)
  private String maintainerUsernameCsv;

  public Software() {}

  public Software(
      Repository repository,
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl) {
    this.repository = repository;
    this.name = name;
    this.type = type;
    this.description = description;
    this.maintainerUsernameCsv = maintainerUsernameCsv;
    this.homeUrl = homeUrl;
  }

  public BigInteger getSoftwareId() {
    return softwareId;
  }

  public void setSoftwareId(BigInteger softwareId) {
    this.softwareId = softwareId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SoftwareType getType() {
    return type;
  }

  public void setType(SoftwareType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public void setHomeUrl(String homeUrl) {
    this.homeUrl = homeUrl;
  }

  public String getMaintainerUsernameCsv() {
    return maintainerUsernameCsv;
  }

  public void setMaintainerUsernameCsv(String maintainerUsernameCsv) {
    this.maintainerUsernameCsv = maintainerUsernameCsv;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Software)) return false;
    Software software = (Software) o;
    return Objects.equals(softwareId, software.softwareId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(softwareId);
  }

  public boolean syncEquals(Software other) {
    boolean equals =
        Objects.equals(name, other.name)
            && Objects.equals(getDescription(), other.getDescription())
            && Objects.equals(getHomeUrl(), other.getHomeUrl());

    // Only compare if other is non-empty.
    if (other.maintainerUsernameCsv != null && !other.maintainerUsernameCsv.isBlank()) {
      equals = equals && Objects.equals(maintainerUsernameCsv, other.maintainerUsernameCsv);
    }

    return equals;
  }
}
