package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import org.jlab.sim.persistence.enumeration.ElementStatus;

/** Whether a product has an SQA element, as somebody recorded it; see 02_ddl.sql. */
@Entity
@Table(name = "SOFTWARE_ELEMENT", schema = "SIM_OWNER")
public class SoftwareElement implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId private SoftwareElementId id;

  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS", nullable = false, length = 16)
  private ElementStatus status;

  /** The evidence of an element that is met, or the issue that tracks a gap */
  @Column(name = "LINK_URL", length = 512)
  private String linkUrl;

  @Column(name = "NOTE", length = 512)
  private String note;

  @Column(name = "RECORDED_BY", nullable = false, length = 64)
  private String recordedBy;

  @Column(name = "RECORDED_DATE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date recordedDate;

  public SoftwareElement() {}

  public SoftwareElement(BigInteger softwareId, String elementId) {
    this.id = new SoftwareElementId(softwareId, elementId);
  }

  public SoftwareElement(BigInteger softwareId, String elementId, ElementStatus status) {
    this(softwareId, elementId);
    this.status = status;
  }

  public SoftwareElementId getId() {
    return id;
  }

  public BigInteger getSoftwareId() {
    return id.getSoftwareId();
  }

  public String getElementId() {
    return id.getElementId();
  }

  public ElementStatus getStatus() {
    return status;
  }

  public void setStatus(ElementStatus status) {
    this.status = status;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getRecordedBy() {
    return recordedBy;
  }

  public void setRecordedBy(String recordedBy) {
    this.recordedBy = recordedBy;
  }

  public Date getRecordedDate() {
    return recordedDate == null ? null : new Date(recordedDate.getTime());
  }

  public void setRecordedDate(Date recordedDate) {
    this.recordedDate = new Date(recordedDate.getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SoftwareElement)) return false;
    SoftwareElement that = (SoftwareElement) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
