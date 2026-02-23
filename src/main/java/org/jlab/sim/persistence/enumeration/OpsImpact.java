package org.jlab.sim.persistence.enumeration;

public enum OpsImpact {
  MARGINAL(1),
  LOW(2),
  MEDIUM(3),
  HIGH(4),
  CRITICAL(5);

  private int value;

  OpsImpact(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static OpsImpact fromValue(int value) {
    for (OpsImpact obj : OpsImpact.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
