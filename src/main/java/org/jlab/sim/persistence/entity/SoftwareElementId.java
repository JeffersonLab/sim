package org.jlab.sim.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class SoftwareElementId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "SOFTWARE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger softwareId;

  @Column(name = "ELEMENT_ID", nullable = false, length = 64)
  private String elementId;

  public SoftwareElementId() {}

  public SoftwareElementId(BigInteger softwareId, String elementId) {
    this.softwareId = softwareId;
    this.elementId = elementId;
  }

  public BigInteger getSoftwareId() {
    return softwareId;
  }

  public String getElementId() {
    return elementId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SoftwareElementId)) return false;
    SoftwareElementId that = (SoftwareElementId) o;
    return Objects.equals(softwareId, that.softwareId) && Objects.equals(elementId, that.elementId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(softwareId, elementId);
  }
}
