package org.jlab.sim.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TOPIC", schema = "SIM_OWNER")
public class Topic implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "TOPIC_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger topicId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NAME", nullable = false, length = 128)
  private String name;

  public Topic() {}

  public BigInteger getTopicId() {
    return topicId;
  }

  public void setTopicId(BigInteger topicId) {
    this.topicId = topicId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Topic)) return false;
    Topic that = (Topic) o;
    return Objects.equals(topicId, that.topicId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(topicId);
  }
}
