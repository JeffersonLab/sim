package org.jlab.sim.persistence.enumeration;

public enum DowntimeProbability {
  MARGINAL(1),
  LOW(2),
  MEDIUM(3),
  HIGH(4),
  CRITICAL(5);

  private int value;

  DowntimeProbability(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static DowntimeProbability fromValue(int value) {
    for (DowntimeProbability obj : DowntimeProbability.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
