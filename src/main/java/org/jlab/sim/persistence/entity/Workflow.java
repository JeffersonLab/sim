package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * How software is released, such as the certified workflow or CSUE; see 02_ddl.sql. Where software
 * lives is its {@link Repository}. The rows are kept in the database, and are read only here.
 */
@Entity
@Table(name = "WORKFLOW", schema = "SIM_OWNER")
public class Workflow implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "WORKFLOW_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger workflowId;

  /** As the lists show it, such as "Certified"; the syncs propose a workflow by it */
  @Column(name = "NAME", nullable = false, length = 64)
  private String name;

  @Column(name = "DESCRIPTION", length = 256)
  private String description;

  /** The workflow's place in the lists */
  @Column(name = "WEIGHT", nullable = false)
  private int weight;

  public Workflow() {}

  public Workflow(BigInteger workflowId, String name) {
    this.workflowId = workflowId;
    this.name = name;
  }

  public BigInteger getWorkflowId() {
    return workflowId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getWeight() {
    return weight;
  }

  /**
   * The workflow as a sentence names it, such as "the Certified workflow": what provides an SQA
   * element on a checklist, told apart from a repository of the same name, as CSUE has.
   */
  public String getPhrase() {
    return "the " + name + " workflow";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Workflow)) return false;
    Workflow that = (Workflow) o;
    return Objects.equals(workflowId, that.workflowId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(workflowId);
  }
}
