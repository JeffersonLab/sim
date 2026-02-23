package org.jlab.sim.persistence.enumeration;

public enum OpsImpact {
  Marginal(1),
  Low(2),
  Medium(3),
  High(4),
  Critical(5);

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
