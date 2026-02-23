package org.jlab.sim.persistence.enumeration;

public enum DowntimeRisk {
  Negligible(1),
  Minimal(2),
  Low(3),
  Minor(4),
  Moderate(5),
  Significant(6),
  High(7),
  Major(8),
  Critical(9),
  Catastrophic(10);

  private int value;

  DowntimeRisk(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static DowntimeRisk fromValue(int value) {
    for (DowntimeRisk obj : DowntimeRisk.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
