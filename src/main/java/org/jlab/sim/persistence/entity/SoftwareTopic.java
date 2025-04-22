package org.jlab.sim.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SOFTWARE_TOPIC", schema = "SIM_OWNER")
public class SoftwareTopic implements Serializable, Comparable<SoftwareTopic> {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  @Basic(optional = false)
  @NotNull
  private SoftwareTopicId softwareTopicId;

  @ManyToOne
  @MapsId("softwareId")
  @JoinColumn(name = "SOFTWARE_ID", referencedColumnName = "SOFTWARE_ID", nullable = false)
  private Software software;

  @ManyToOne
  @JoinColumn(name = "TOPIC_ID", referencedColumnName = "TOPIC_ID", nullable = false)
  @MapsId("topicId")
  private Topic topic;

  public SoftwareTopicId getSoftwareTopicId() {
    return softwareTopicId;
  }

  public void setSoftwareTopicId(SoftwareTopicId softwareTopicId) {
    this.softwareTopicId = softwareTopicId;
  }

  public Software getSoftware() {
    return software;
  }

  public void setSoftware(Software software) {
    this.software = software;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public SoftwareTopic() {}

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SoftwareTopic)) return false;
    SoftwareTopic that = (SoftwareTopic) o;
    return Objects.equals(softwareTopicId, that.softwareTopicId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(softwareTopicId);
  }

  @Override
  public int compareTo(SoftwareTopic o) {
    return this.topic.getName().compareTo(o.topic.getName());
  }
}
