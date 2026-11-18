package org.jlab.sim.persistence.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.sim.persistence.entity.SoftwareElement;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.enumeration.ControlLevel;
import org.jlab.sim.persistence.enumeration.ElementRequirement;
import org.jlab.sim.persistence.enumeration.ElementStatus;

/**
 * A product's checklist of SQA elements: the elements the ACS SQAP requires or recommends at the
 * product's control level, each with what somebody recorded of it, if anybody has, and with who
 * provides it for the product, if anybody does.
 *
 * <p>An element's status is what SIM computes from its own records, for the few elements it can
 * (see {@link ComputedElements}); otherwise what was recorded for the product; otherwise met, if a
 * provider, the product's workflow or its repository, provides the element for all its software;
 * otherwise none.
 */
public final class Checklist {

  /**
   * An element that something provides for all the software it holds, so that it need not be
   * recorded on each product: the provider, as the dialog names it after "Met, by", such as a
   * repository's name or "the Certified workflow", and on what basis.
   */
  public static final class Provision {
    private final String elementId;
    private final String provider;
    private final String note;
    private final String linkUrl;

    public Provision(String elementId, String provider, String note, String linkUrl) {
      this.elementId = elementId;
      this.provider = provider;
      this.note = note;
      this.linkUrl = linkUrl;
    }

    public String getElementId() {
      return elementId;
    }

    public String getProvider() {
      return provider;
    }

    public String getNote() {
      return note;
    }

    public String getLinkUrl() {
      return linkUrl;
    }
  }

  /**
   * The status of an element that SIM computes from its own records of the product, such as its
   * maintainers, so that nobody records it: Met or Gap, and what it was computed from.
   */
  public static final class Finding {
    private final String elementId;
    private final ElementStatus status;
    private final String note;

    public Finding(String elementId, ElementStatus status, String note) {
      this.elementId = elementId;
      this.status = status;
      this.note = note;
    }

    public String getElementId() {
      return elementId;
    }

    public ElementStatus getStatus() {
      return status;
    }

    public String getNote() {
      return note;
    }
  }

  /**
   * An element of the checklist, with what somebody recorded of it for the product, if anybody, its
   * provision, if it is provided, and its finding, if SIM computes it.
   */
  public static final class Item {
    private final SqaElement element;
    private final ElementRequirement requirement;
    private final Provision provision;
    private final Finding finding;
    // What was recorded: each stays unset if nobody has recorded the element
    private ElementStatus status;
    private String linkUrl;
    private String note;
    private String recordedBy;
    private Date recordedDate;

    Item(
        SqaElement element,
        ElementRequirement requirement,
        SoftwareElement recorded,
        Provision provision,
        Finding finding) {
      this.element = element;
      this.requirement = requirement;
      this.provision = provision;
      this.finding = finding;
      // What was recorded of a computed element, before SIM computed it, no longer counts
      if (recorded != null && finding == null) {
        this.status = recorded.getStatus();
        this.linkUrl = recorded.getLinkUrl();
        this.note = recorded.getNote();
        this.recordedBy = recorded.getRecordedBy();
        this.recordedDate = recorded.getRecordedDate();
      }
    }

    public SqaElement getElement() {
      return element;
    }

    public ElementRequirement getRequirement() {
      return requirement;
    }

    /** The status recorded for the product, or null if nobody has recorded one. */
    public ElementStatus getStatus() {
      return status;
    }

    /** How the element is provided for the product, or null if nothing provides it. */
    public Provision getProvision() {
      return provision;
    }

    /** What SIM computed of the element, or null if it is not one SIM computes. */
    public Finding getFinding() {
      return finding;
    }

    /**
     * The status that counts: the computed one, if SIM computes the element; otherwise the recorded
     * one; otherwise met, if the element is provided; otherwise null.
     */
    public ElementStatus getEffectiveStatus() {
      if (finding != null) {
        return finding.getStatus();
      }
      if (status != null) {
        return status;
      }
      return provision == null ? null : ElementStatus.MET;
    }

    public String getLinkUrl() {
      return linkUrl;
    }

    public String getNote() {
      return note;
    }

    public String getRecordedBy() {
      return recordedBy;
    }

    public Date getRecordedDate() {
      return recordedDate == null ? null : new Date(recordedDate.getTime());
    }
  }

  private final List<Item> items;

  private Checklist(List<Item> items) {
    this.items = List.copyOf(items);
  }

  /**
   * The checklist of a product at a control level.
   *
   * @param elements The SQAP's elements
   * @param level The product's control level: the override where there is one
   * @param recorded What has been recorded for the product, of any element. An element the level
   *     neither requires nor recommends is left out, whatever was recorded of it at another level.
   * @param provided The elements provided for the product without being recorded on it: those its
   *     workflow provides and those its repository provides. Where two provide the same element,
   *     the first names the provider.
   * @return The required elements, then the recommended ones, each in the SQAP's order
   */
  public static Checklist of(
      Collection<SqaElement> elements,
      ControlLevel level,
      Collection<SoftwareElement> recorded,
      Collection<Provision> provided) {
    return of(elements, level, recorded, provided, List.of());
  }

  /**
   * The checklist of a product at a control level, with the elements SIM computes.
   *
   * @param computed What SIM computed of the product's elements: each replaces what was recorded or
   *     provided of its element
   * @see #of(Collection, ControlLevel, Collection, Collection)
   */
  public static Checklist of(
      Collection<SqaElement> elements,
      ControlLevel level,
      Collection<SoftwareElement> recorded,
      Collection<Provision> provided,
      Collection<Finding> computed) {
    Map<String, SoftwareElement> byId = new HashMap<>();
    for (SoftwareElement softwareElement : recorded) {
      byId.put(softwareElement.getElementId(), softwareElement);
    }
    Map<String, Provision> provisions = new HashMap<>();
    for (Provision provision : provided) {
      provisions.putIfAbsent(provision.getElementId(), provision);
    }
    Map<String, Finding> findings = new HashMap<>();
    for (Finding finding : computed) {
      findings.put(finding.getElementId(), finding);
    }

    List<Item> items = new ArrayList<>();
    for (SqaElement element : elements) {
      ElementRequirement requirement = element.getRequirement(level);
      if (requirement != null) {
        items.add(
            new Item(
                element,
                requirement,
                byId.get(element.getElementId()),
                provisions.get(element.getElementId()),
                findings.get(element.getElementId())));
      }
    }
    items.sort(
        Comparator.comparing(Item::getRequirement)
            .thenComparingInt((Item item) -> item.getElement().getWeight()));

    return new Checklist(items);
  }

  public List<Item> getItems() {
    return items;
  }

  /**
   * Whether anybody has recorded a status for any element of the checklist. What is only provided
   * or computed doesn't count: software that has not yet come under the SQAP shows as not recorded.
   */
  public boolean isRecorded() {
    return items.stream().anyMatch(item -> item.getStatus() != null);
  }

  /** The required elements that apply to the product: all but those recorded as not applicable. */
  public long getRequiredCount() {
    return items.stream()
        .filter(item -> item.getRequirement() == ElementRequirement.REQUIRED)
        .filter(item -> item.getEffectiveStatus() != ElementStatus.NOT_APPLICABLE)
        .count();
  }

  public long getRequiredMetCount() {
    return items.stream()
        .filter(item -> item.getRequirement() == ElementRequirement.REQUIRED)
        .filter(item -> item.getEffectiveStatus() == ElementStatus.MET)
        .count();
  }

  /** The elements recorded as a gap, required or recommended. */
  public long getGapCount() {
    return items.stream().filter(item -> item.getEffectiveStatus() == ElementStatus.GAP).count();
  }

  /** Such as "5 of 7": the required elements that are met, of those that apply. */
  public String getSummary() {
    return getRequiredMetCount() + " of " + getRequiredCount();
  }
}
