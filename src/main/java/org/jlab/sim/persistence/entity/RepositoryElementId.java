package org.jlab.sim.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class RepositoryElementId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "REPOSITORY_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger repositoryId;

  @Column(name = "ELEMENT_ID", nullable = false, length = 64)
  private String elementId;

  public RepositoryElementId() {}

  public RepositoryElementId(BigInteger repositoryId, String elementId) {
    this.repositoryId = repositoryId;
    this.elementId = elementId;
  }

  public BigInteger getRepositoryId() {
    return repositoryId;
  }

  public String getElementId() {
    return elementId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RepositoryElementId)) return false;
    RepositoryElementId that = (RepositoryElementId) o;
    return Objects.equals(repositoryId, that.repositoryId)
        && Objects.equals(elementId, that.elementId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repositoryId, elementId);
  }
}
