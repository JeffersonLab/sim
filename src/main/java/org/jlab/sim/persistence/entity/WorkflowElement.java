package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * An SQA element a workflow provides for all the software released with it, such as a folder for
 * each installed version; see 02_ddl.sql. The rows are kept in the database, and are read only
 * here.
 */
@Entity
@Table(name = "WORKFLOW_ELEMENT", schema = "SIM_OWNER")
public class WorkflowElement implements ProvidedElement, Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId private WorkflowElementId id;

  /** On what basis the workflow provides the element */
  @Column(name = "NOTE", nullable = false, length = 512)
  private String note;

  @Column(name = "LINK_URL", length = 512)
  private String linkUrl;

  public WorkflowElement() {}

  public BigInteger getWorkflowId() {
    return id.getWorkflowId();
  }

  @Override
  public String getElementId() {
    return id.getElementId();
  }

  @Override
  public String getNote() {
    return note;
  }

  @Override
  public String getLinkUrl() {
    return linkUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof WorkflowElement)) return false;
    WorkflowElement that = (WorkflowElement) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
