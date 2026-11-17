package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import org.jlab.sim.persistence.enumeration.ControlLevel;
import org.jlab.sim.persistence.enumeration.ElementRequirement;

/**
 * An SQA element the ACS SQAP requires of a product as a whole, such as a User Guide, and at which
 * control levels. The rows are loaded from the SQAP's elements.json (see 02_ddl.sql), and are read
 * only here.
 */
@Entity
@Table(name = "SQA_ELEMENT", schema = "SIM_OWNER")
public class SqaElement implements Serializable {
  private static final long serialVersionUID = 1L;

  /** The SQAP's id for the element, which stays the same across its revisions */
  @Id
  @Column(name = "ELEMENT_ID", nullable = false, length = 64)
  private String elementId;

  @Column(name = "NAME", nullable = false, length = 256)
  private String name;

  @Column(name = "PHASE", nullable = false, length = 64)
  private String phase;

  @Column(name = "SECTION_NUMBER", nullable = false, length = 16)
  private String sectionNumber;

  @Column(name = "SECTION_URL", nullable = false, length = 256)
  private String sectionUrl;

  /** The element's place in the SQAP's order */
  @Column(name = "WEIGHT", nullable = false)
  private int weight;

  @Enumerated(EnumType.STRING)
  @Column(name = "LEVEL_0", length = 16)
  private ElementRequirement level0;

  @Enumerated(EnumType.STRING)
  @Column(name = "LEVEL_1", length = 16)
  private ElementRequirement level1;

  @Enumerated(EnumType.STRING)
  @Column(name = "LEVEL_2", length = 16)
  private ElementRequirement level2;

  @Enumerated(EnumType.STRING)
  @Column(name = "LEVEL_3", length = 16)
  private ElementRequirement level3;

  @Column(name = "SQAP_VERSION", nullable = false, length = 64)
  private String sqapVersion;

  public SqaElement() {}

  /**
   * An element as the SQAP gives it; the requirements are for Minimal, Partial, Significant, Full.
   */
  public SqaElement(String elementId, String name, int weight, ElementRequirement... requirements) {
    this.elementId = elementId;
    this.name = name;
    this.weight = weight;
    this.level0 = requirements[0];
    this.level1 = requirements[1];
    this.level2 = requirements[2];
    this.level3 = requirements[3];
  }

  public String getElementId() {
    return elementId;
  }

  public String getName() {
    return name;
  }

  public String getPhase() {
    return phase;
  }

  public String getSectionNumber() {
    return sectionNumber;
  }

  public String getSectionUrl() {
    return sectionUrl;
  }

  public int getWeight() {
    return weight;
  }

  public String getSqapVersion() {
    return sqapVersion;
  }

  /**
   * Whether the SQAP requires or recommends the element at a control level.
   *
   * @return null if it does neither
   */
  public ElementRequirement getRequirement(ControlLevel level) {
    return switch (level) {
      case Minimal -> level0;
      case Partial -> level1;
      case Significant -> level2;
      case Full -> level3;
    };
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SqaElement)) return false;
    SqaElement that = (SqaElement) o;
    return Objects.equals(elementId, that.elementId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(elementId);
  }
}
