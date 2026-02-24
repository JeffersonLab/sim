package org.jlab.sim.persistence.enumeration;

public enum CodeComplexity {
  Simple(1),
  Low(2),
  Medium(3),
  High(4),
  Complex(5);

  private int value;

  CodeComplexity(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static CodeComplexity fromValue(int value) {
    for (CodeComplexity obj : CodeComplexity.values()) {
      if (obj.value == value) {
        return obj;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
