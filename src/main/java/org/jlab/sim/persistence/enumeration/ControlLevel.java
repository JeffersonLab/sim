package org.jlab.sim.persistence.enumeration;

/**
 * The ACS SQAP's software control level, derived from Ops Impact and Code Complexity by the
 * database (see 02_ddl.sql), or set as an override with a rationale.
 */
public enum ControlLevel {
  Minimal(0),
  Partial(1),
  Significant(2),
  Full(3);

  private final int value;

  ControlLevel(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static ControlLevel fromValue(int value) {
    for (ControlLevel obj : ControlLevel.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
