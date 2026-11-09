package org.jlab.sim.persistence.enumeration;

/**
 * How much attention a software's downtime risk calls for, derived from Ops Impact and Fragility by
 * the database (see 02_ddl.sql).
 */
public enum Attention {
  Low(1),
  Moderate(2),
  High(3),
  Severe(4);

  private final int value;

  Attention(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Attention fromValue(int value) {
    for (Attention obj : Attention.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
