package org.jlab.sim.persistence.enumeration;

/**
 * Whether a product has an SQA element, as somebody recorded it. An element nobody has recorded has
 * no status, which is not a gap.
 */
public enum ElementStatus {
  MET("Met"),
  GAP("Gap"),
  NOT_APPLICABLE("Not applicable");

  private final String label;

  ElementStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
