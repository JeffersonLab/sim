package org.jlab.sim.persistence.enumeration;

public enum DebugTestDifficulty {
  Easy(1),
  Low(2),
  Medium(3),
  High(4),
  Hard(5);

  private int value;

  DebugTestDifficulty(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static DebugTestDifficulty fromValue(int value) {
    for (DebugTestDifficulty obj : DebugTestDifficulty.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
