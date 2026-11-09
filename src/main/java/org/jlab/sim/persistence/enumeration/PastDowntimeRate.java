package org.jlab.sim.persistence.enumeration;

public enum PastDowntimeRate {
  Rare(1),
  Few(2),
  Some(3),
  Many(4),
  Frequent(5);

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
