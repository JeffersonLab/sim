package org.jlab.sim.persistence.enumeration;

/** Whether the ACS SQAP requires or recommends an SQA element at a control level. */
public enum ElementRequirement {
  REQUIRED("Required"),
  RECOMMENDED("Recommended");

  private final String label;

  ElementRequirement(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
