package org.jlab.sim.persistence.enumeration;

public enum PastDowntimeRate {
  Marginal(1),
  Low(2),
  Medium(3),
  High(4),
  Critical(5);

  private int value;

  PastDowntimeRate(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static PastDowntimeRate fromValue(int value) {
    for (PastDowntimeRate obj : PastDowntimeRate.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
