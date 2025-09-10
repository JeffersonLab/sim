package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class SoftwareTopicId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Basic(optional = false)
  @NotNull
  @Column(name = "SOFTWARE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger softwareId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "TOPIC_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger topicId;

  public SoftwareTopicId() {}

  public SoftwareTopicId(BigInteger softwareId, BigInteger topicId) {
    this.softwareId = softwareId;
    this.topicId = topicId;
  }

  public BigInteger getSoftwareId() {
    return softwareId;
  }

  public void setSoftwareId(BigInteger softwareId) {
    this.softwareId = softwareId;
  }

  public BigInteger getTopicId() {
    return topicId;
  }

  public void setTopicId(BigInteger topicId) {
    this.topicId = topicId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SoftwareTopicId)) return false;
    SoftwareTopicId that = (SoftwareTopicId) o;
    return Objects.equals(softwareId, that.softwareId) && Objects.equals(topicId, that.topicId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(softwareId, topicId);
  }
}
