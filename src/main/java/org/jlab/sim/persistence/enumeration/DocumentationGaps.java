package org.jlab.sim.persistence.enumeration;

public enum DocumentationGaps {
  Few(1),
  Low(2),
  Medium(3),
  High(4),
  Many(5);

  private int value;

  DocumentationGaps(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static DocumentationGaps fromValue(int value) {
    for (DocumentationGaps obj : DocumentationGaps.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
