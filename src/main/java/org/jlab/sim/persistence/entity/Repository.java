package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;

@Entity
@Table(name = "REPOSITORY", schema = "SIM_OWNER")
public class Repository implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "REPOSITORY_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger repositoryId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NAME", nullable = false, length = 128)
  private String name;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "DESCRIPTION", nullable = false, length = 256)
  private String description;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "HOME_URL", nullable = false, length = 256)
  private String homeUrl;

  @Basic
  @Column(name = "SYNC_YN", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean sync;

  public Repository() {}

  public BigInteger getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(BigInteger repositoryId) {
    this.repositoryId = repositoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public void setHomeUrl(String homeUrl) {
    this.homeUrl = homeUrl;
  }

  public boolean isSync() {
    return sync;
  }

  public void setSync(boolean sync) {
    this.sync = sync;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Repository)) return false;
    Repository that = (Repository) o;
    return Objects.equals(repositoryId, that.repositoryId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(repositoryId);
  }
}
