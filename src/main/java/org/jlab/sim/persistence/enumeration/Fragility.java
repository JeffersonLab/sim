package org.jlab.sim.persistence.enumeration;

/** The worst of the five risk factors, derived by the database (see 02_ddl.sql). */
public enum Fragility {
  Robust(1),
  Low(2),
  Medium(3),
  High(4),
  Fragile(5);

  private final int value;

  Fragility(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Fragility fromValue(int value) {
    for (Fragility obj : Fragility.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
