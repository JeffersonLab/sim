package org.jlab.sim.persistence.enumeration;

public enum Esotericism {
  Marginal(1),
  Low(2),
  Medium(3),
  High(4),
  Critical(5);

  private int value;

  Esotericism(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Esotericism fromValue(int value) {
    for (Esotericism obj : Esotericism.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
