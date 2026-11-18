package org.jlab.sim.business.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jlab.sim.persistence.entity.RepositoryElement;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareElement;
import org.jlab.sim.persistence.entity.SoftwareElementId;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.entity.WorkflowElement;
import org.jlab.sim.persistence.enumeration.ElementStatus;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

/** The ACS SQAP's SQA elements, and what has been recorded of them for each product. */
@Stateless
public class SqaElementService extends JPAService<SqaElement> {

  /** The filter value for a gap in any element, where an element's id names one */
  public static final String ANY_ELEMENT = "ANY";

  private static final int MAX_TEXT = 512;
  private static final int MAX_USERNAME = 64;
  private static final Pattern WEB_URL = Pattern.compile("https?://\\S+");

  /** What to record of one element for a product; a null status clears what was recorded. */
  public record Entry(String elementId, ElementStatus status, String linkUrl, String note) {}

  public SqaElementService() {
    super(SqaElement.class);
  }

  /** The SQAP's elements, in its order. */
  @PermitAll
  public List<SqaElement> getAll() {
    return getEntityManager()
        .createQuery("select e from SqaElement e order by e.weight", SqaElement.class)
        .getResultList();
  }

  /**
   * What has been recorded for each of these products.
   *
   * @return By software id; a product with nothing recorded has no entry
   */
  @PermitAll
  public Map<BigInteger, List<SoftwareElement>> findRecorded(List<Software> softwareList) {
    Map<BigInteger, List<SoftwareElement>> recorded = new HashMap<>();
    if (softwareList.isEmpty()) {
      return recorded;
    }

    List<BigInteger> ids = softwareList.stream().map(Software::getSoftwareId).toList();
    List<SoftwareElement> found =
        getEntityManager()
            .createQuery(
                "select se from SoftwareElement se where se.id.softwareId in :ids",
                SoftwareElement.class)
            .setParameter("ids", ids)
            .getResultList();
    for (SoftwareElement softwareElement : found) {
      recorded
          .computeIfAbsent(softwareElement.getSoftwareId(), id -> new ArrayList<>())
          .add(softwareElement);
    }
    return recorded;
  }

  /**
   * The elements each repository provides for all its software.
   *
   * @return By repository id; a repository that provides none has no entry
   */
  @PermitAll
  public Map<BigInteger, List<RepositoryElement>> findProvided() {
    Map<BigInteger, List<RepositoryElement>> provided = new HashMap<>();
    List<RepositoryElement> found =
        getEntityManager()
            .createQuery("select re from RepositoryElement re", RepositoryElement.class)
            .getResultList();
    for (RepositoryElement repositoryElement : found) {
      provided
          .computeIfAbsent(repositoryElement.getRepositoryId(), id -> new ArrayList<>())
          .add(repositoryElement);
    }
    return provided;
  }

  /**
   * The elements each workflow provides for all the software released with it.
   *
   * @return By workflow id; a workflow that provides none has no entry
   */
  @PermitAll
  public Map<BigInteger, List<WorkflowElement>> findProvidedByWorkflow() {
    Map<BigInteger, List<WorkflowElement>> provided = new HashMap<>();
    List<WorkflowElement> found =
        getEntityManager()
            .createQuery("select we from WorkflowElement we", WorkflowElement.class)
            .getResultList();
    for (WorkflowElement workflowElement : found) {
      provided
          .computeIfAbsent(workflowElement.getWorkflowId(), id -> new ArrayList<>())
          .add(workflowElement);
    }
    return provided;
  }

  /**
   * Records a product's elements. Elements that are not among the entries stay as they are, and an
   * entry that changes nothing keeps who recorded it and when.
   */
  @RolesAllowed({SoftwareService.ADMIN_ROLE, SoftwareService.ACG_ROLE})
  public void record(BigInteger softwareId, List<Entry> entries, String recordedBy)
      throws UserFriendlyException {
    if (softwareId == null) {
      throw new UserFriendlyException("softwareId cannot be empty");
    }

    if (getEntityManager().find(Software.class, softwareId) == null) {
      throw new UserFriendlyException("software not found with id: " + softwareId);
    }

    if (recordedBy == null || recordedBy.isBlank()) {
      throw new UserFriendlyException("The recording user is unknown");
    }
    String username =
        recordedBy.length() > MAX_USERNAME ? recordedBy.substring(0, MAX_USERNAME) : recordedBy;

    for (Entry entry : entries) {
      SqaElement element = getEntityManager().find(SqaElement.class, entry.elementId());
      if (element == null) {
        throw new UserFriendlyException("Unknown SQA element: " + entry.elementId());
      }

      String linkUrl = blankToNull(entry.linkUrl());
      String note = blankToNull(entry.note());
      validate(element, entry.status(), linkUrl, note);

      SoftwareElementId id = new SoftwareElementId(softwareId, element.getElementId());
      SoftwareElement existing = getEntityManager().find(SoftwareElement.class, id);

      if (entry.status() == null) {
        if (existing != null) {
          getEntityManager().remove(existing);
        }
      } else if (existing == null
          || existing.getStatus() != entry.status()
          || !Objects.equals(existing.getLinkUrl(), linkUrl)
          || !Objects.equals(existing.getNote(), note)) {
        SoftwareElement softwareElement =
            existing == null ? new SoftwareElement(softwareId, element.getElementId()) : existing;
        softwareElement.setStatus(entry.status());
        softwareElement.setLinkUrl(linkUrl);
        softwareElement.setNote(note);
        softwareElement.setRecordedBy(username);
        softwareElement.setRecordedDate(new Date());
        if (existing == null) {
          getEntityManager().persist(softwareElement);
        }
      }
    }
  }

  private static void validate(
      SqaElement element, ElementStatus status, String linkUrl, String note)
      throws UserFriendlyException {
    String name = "\"" + element.getShortName() + "\"";

    if (status == null) {
      if (linkUrl != null || note != null) {
        throw new UserFriendlyException(name + " needs a status to keep its link or note");
      }
      return;
    }

    if (status == ElementStatus.NOT_APPLICABLE && note == null) {
      throw new UserFriendlyException(name + " needs a note saying why it is not applicable");
    }

    if (linkUrl != null && (linkUrl.length() > MAX_TEXT || !WEB_URL.matcher(linkUrl).matches())) {
      throw new UserFriendlyException(
          name + " needs a link that starts with http:// or https://, of 512 characters or fewer");
    }

    if (note != null && note.length() > MAX_TEXT) {
      throw new UserFriendlyException(name + " needs a note of 512 characters or fewer");
    }
  }

  private static String blankToNull(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
