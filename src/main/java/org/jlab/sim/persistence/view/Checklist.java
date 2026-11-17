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
 * product's control level, each with what somebody recorded of it, if anybody has.
 */
public final class Checklist {

  /** An element of the checklist, with what somebody recorded of it for the product, if anybody. */
  public static final class Item {
    private final SqaElement element;
    private final ElementRequirement requirement;
    // What was recorded: each stays unset if nobody has recorded the element
    private ElementStatus status;
    private String linkUrl;
    private String note;
    private String recordedBy;
    private Date recordedDate;

    Item(SqaElement element, ElementRequirement requirement, SoftwareElement recorded) {
      this.element = element;
      this.requirement = requirement;
      if (recorded != null) {
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

    /** The recorded status, or null if nobody has recorded one. */
    public ElementStatus getStatus() {
      return status;
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
   * @return The required elements, then the recommended ones, each in the SQAP's order
   */
  public static Checklist of(
      Collection<SqaElement> elements, ControlLevel level, Collection<SoftwareElement> recorded) {
    Map<String, SoftwareElement> byId = new HashMap<>();
    for (SoftwareElement softwareElement : recorded) {
      byId.put(softwareElement.getElementId(), softwareElement);
    }

    List<Item> items = new ArrayList<>();
    for (SqaElement element : elements) {
      ElementRequirement requirement = element.getRequirement(level);
      if (requirement != null) {
        items.add(new Item(element, requirement, byId.get(element.getElementId())));
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

  /** Whether anybody has recorded a status for any element of the checklist. */
  public boolean isRecorded() {
    return items.stream().anyMatch(item -> item.getStatus() != null);
  }

  /** The required elements that apply to the product: all but those recorded as not applicable. */
  public long getRequiredCount() {
    return items.stream()
        .filter(item -> item.getRequirement() == ElementRequirement.REQUIRED)
        .filter(item -> item.getStatus() != ElementStatus.NOT_APPLICABLE)
        .count();
  }

  public long getRequiredMetCount() {
    return items.stream()
        .filter(item -> item.getRequirement() == ElementRequirement.REQUIRED)
        .filter(item -> item.getStatus() == ElementStatus.MET)
        .count();
  }

  /** The elements recorded as a gap, required or recommended. */
  public long getGapCount() {
    return items.stream().filter(item -> item.getStatus() == ElementStatus.GAP).count();
  }

  /** Such as "5 of 7": the required elements that are met, of those that apply. */
  public String getSummary() {
    return getRequiredMetCount() + " of " + getRequiredCount();
  }
}
