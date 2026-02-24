package org.jlab.sim.persistence.enumeration;

public enum DowntimeProbability {
  Marginal(1),
  Low(2),
  Medium(3),
  High(4),
  Critical(5);

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
