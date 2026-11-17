package org.jlab.sim.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class WorkflowElementId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "WORKFLOW_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger workflowId;

  @Column(name = "ELEMENT_ID", nullable = false, length = 64)
  private String elementId;

  public WorkflowElementId() {}

  public WorkflowElementId(BigInteger workflowId, String elementId) {
    this.workflowId = workflowId;
    this.elementId = elementId;
  }

  public BigInteger getWorkflowId() {
    return workflowId;
  }

  public String getElementId() {
    return elementId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof WorkflowElementId)) return false;
    WorkflowElementId that = (WorkflowElementId) o;
    return Objects.equals(workflowId, that.workflowId) && Objects.equals(elementId, that.elementId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workflowId, elementId);
  }
}
