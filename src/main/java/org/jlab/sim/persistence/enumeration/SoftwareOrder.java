package org.jlab.sim.persistence.enumeration;

/** The order of a list of software. */
public enum SoftwareOrder {
  /** By name, A to Z */
  NAME,
  /**
   * The most important first: assessed software by Ops Impact from Critical down, then by Attention
   * from Severe down, then by name; then the software nobody has assessed, by name
   */
  IMPACT
}
