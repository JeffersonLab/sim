package org.jlab.sim.business.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareElement;
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.entity.Workflow;
import org.jlab.sim.persistence.enumeration.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  @EJB RepositoryService repositoryService;
  @EJB SoftwareTopicService softwareTopicService;
  @EJB WorkflowService workflowService;

  static final String ACG_ROLE = "acg";
  static final String ADMIN_ROLE = "sim-admin";
  private static final String ASSESSED_DATE = "assessedDate";
  private static final String SOFTWARE_ID = "softwareId";
  private static final String ELEMENT_ID = "elementId";

  public SoftwareService() {
    super(Software.class);
  }

  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void addSoftware(
      BigInteger repoId,
      BigInteger workflowId,
      String name,
      SoftwareType type,
      String[] topicArray,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived,
      String note)
      throws UserFriendlyException {

    if (repoId == null) {
      throw new UserFriendlyException("repoId cannot be empty");
    }

    Repository repo = repositoryService.find(repoId);

    if (repo == null) {
      throw new UserFriendlyException("repo not found with id: " + repoId);
    }

    if (name == null || name.isEmpty()) {
      throw new UserFriendlyException("Name cannot be empty");
    }

    if (type == null) {
      throw new UserFriendlyException("Type cannot be empty");
    }

    Software software =
        new Software(repo, name, type, description, maintainerUsernameCsv, homeUrl, archived, note);
    software.setWorkflow(findWorkflow(workflowId));

    create(software);

    em.flush();

    softwareTopicService.set(software, topicArray);
  }

  /**
   * Edits software.
   *
   * @param setWorkflow Whether to set the workflow: a sync's Update doesn't, so that it never
   *     changes one somebody set
   * @param workflowId The workflow to set, or null for none
   */
  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void editSoftware(
      BigInteger softwareId,
      BigInteger repoId,
      boolean setWorkflow,
      BigInteger workflowId,
      SoftwareType type,
      String[] topicArray,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived,
      String note)
      throws UserFriendlyException {

    if (softwareId == null) {
      throw new UserFriendlyException("softwareId cannot be empty");
    }

    Software software = find(softwareId);

    if (software == null) {
      throw new UserFriendlyException("software not found with id: " + softwareId);
    }

    if (repoId == null) {
      throw new UserFriendlyException("repoId cannot be empty");
    }

    Repository repo = repositoryService.find(repoId);

    if (repo == null) {
      throw new UserFriendlyException("repo not found with id: " + repoId);
    }

    if (type == null) {
      throw new UserFriendlyException("Type cannot be empty");
    }

    if (setWorkflow) {
      software.setWorkflow(findWorkflow(workflowId));
    }

    software.setType(type);
    software.setDescription(description);
    software.setMaintainerUsernameCsv(maintainerUsernameCsv);
    software.setHomeUrl(homeUrl);
    software.setArchived(archived);

    // If note == null we treat this as "leave existing as is"
    if (note != null) {
      software.setNote(note);
    }

    edit(software);

    em.flush();

    softwareTopicService.set(software, topicArray);
  }

  /** The workflow of this id, or null for a null id, which is no workflow. */
  private Workflow findWorkflow(BigInteger workflowId) throws UserFriendlyException {
    if (workflowId == null) {
      return null;
    }

    Workflow workflow = workflowService.find(workflowId);

    if (workflow == null) {
      throw new UserFriendlyException("workflow not found with id: " + workflowId);
    }

    return workflow;
  }

  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void editSoftwareRisk(
      BigInteger softwareId,
      OpsImpact impact,
      PastDowntimeRate rate,
      DebugTestDifficulty difficulty,
      CodeComplexity complexity,
      DocumentationGaps gaps,
      Esotericism esotericism,
      ControlLevel controlLevelOverride,
      String controlLevelNote,
      String assessedBy)
      throws UserFriendlyException {

    if (softwareId == null) {
      throw new UserFriendlyException("softwareId cannot be empty");
    }

    Software software = find(softwareId);

    if (software == null) {
      throw new UserFriendlyException("software not found with id: " + softwareId);
    }

    if (impact == null
        || rate == null
        || difficulty == null
        || complexity == null
        || gaps == null
        || esotericism == null) {
      throw new UserFriendlyException("Each risk input must be set");
    }

    if (controlLevelOverride != null && (controlLevelNote == null || controlLevelNote.isBlank())) {
      throw new UserFriendlyException("A control level override needs a rationale");
    }

    if (controlLevelNote != null && controlLevelNote.length() > 512) {
      throw new UserFriendlyException("The rationale must be 512 characters or fewer");
    }

    if (assessedBy == null || assessedBy.isBlank()) {
      throw new UserFriendlyException("The assessing user is unknown");
    }

    software.setImpact(impact);
    software.setRate(rate);
    software.setDifficulty(difficulty);
    software.setComplexity(complexity);
    software.setGaps(gaps);
    software.setEsotericism(esotericism);
    software.setControlLevelOverride(controlLevelOverride);
    // The rationale belongs to the override
    software.setControlLevelNote(controlLevelOverride == null ? null : controlLevelNote.trim());
    software.setAssessedBy(assessedBy.length() > 64 ? assessedBy.substring(0, 64) : assessedBy);
    software.setAssessedDate(new Date());

    edit(software);
  }

  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void removeSoftware(BigInteger softwareId) throws UserFriendlyException {
    if (softwareId == null) {
      throw new UserFriendlyException("softwareId cannot be empty");
    }

    Software software = find(softwareId);

    if (software == null) {
      throw new UserFriendlyException("software not found with id: " + softwareId);
    }

    for (SoftwareTopic st : software.getSoftwareTopicList()) {
      em.remove(st);
    }

    remove(software);
  }

  @PermitAll
  @Override
  public List<Software> findAll(OrderDirective... directives) {
    return super.findAll(directives);
  }

  /**
   * The software the filters select, a page of it.
   *
   * @param workflow Software released with this workflow, or null for any
   * @param noWorkflow Software that has no workflow, whatever {@code workflow} is
   */
  @PermitAll
  public List<Software> filterList(
      String softwareName,
      String username,
      Repository repository,
      Workflow workflow,
      boolean noWorkflow,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed,
      String gapElementId,
      SoftwareOrder order,
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Software> cq = cb.createQuery(Software.class);
    Root<Software> root = cq.from(Software.class);
    cq.select(root);

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            softwareName,
            username,
            repository,
            workflow,
            noWorkflow,
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed,
            gapElementId);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    if (order == SoftwareOrder.IMPACT) {
      // Unassessed software shows no impact or attention, so it comes after all that is assessed.
      // Its inputs are still the defaults, which leaves it in name order.
      orders.add(
          cb.asc(
              cb.<Integer>selectCase().when(cb.isNull(root.get(ASSESSED_DATE)), 1).otherwise(0)));
      orders.add(cb.desc(root.get("impact")));
      orders.add(cb.desc(root.get("attention")));
    }
    orders.add(cb.asc(root.get("name")));
    // Two repositories may hold software of the same name, and pages need a fixed order
    orders.add(cb.asc(root.get(SOFTWARE_ID)));
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<Software> root,
      String softwareName,
      String username,
      Repository repository,
      Workflow workflow,
      boolean noWorkflow,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed,
      String gapElementId) {
    List<Predicate> filters = new ArrayList<>();

    if (softwareName != null && !softwareName.isEmpty()) {
      String softwareNameEscaped = softwareName.replaceAll("\\*", "%");
      filters.add(cb.like(cb.lower(root.get("name")), softwareNameEscaped.toLowerCase(Locale.US)));
    }

    if (username != null && !username.isEmpty()) {
      String usernameEscaped = username.replaceAll("\\*", "%");
      String usernameWrapped = "%" + usernameEscaped + "%";
      filters.add(
          cb.like(
              cb.lower(root.get("maintainerUsernameCsv")), usernameWrapped.toLowerCase(Locale.US)));
    }

    if (repository != null) {
      filters.add(
          cb.equal(root.get("repository").get("repositoryId"), repository.getRepositoryId()));
    }

    if (noWorkflow) {
      filters.add(cb.isNull(root.get("workflow")));
    } else if (workflow != null) {
      filters.add(cb.equal(root.get("workflow").get("workflowId"), workflow.getWorkflowId()));
    }

    if (type != null) {
      filters.add(cb.equal(root.get("type"), type));
    }

    if (topicNameArray != null && topicNameArray.length > 0) {
      for (String topicName : topicNameArray) {
        Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
        Root<SoftwareTopic> subqueryRoot = subquery.from(SoftwareTopic.class);
        subquery.select(subqueryRoot.get("software").get(SOFTWARE_ID));
        subquery.where(cb.equal(subqueryRoot.get("topic").get("name"), topicName));
        filters.add(cb.in(root.get(SOFTWARE_ID)).value(subquery));
      }
    }

    if (includeArchived == null) {
      filters.add(cb.equal(root.get("archived"), false));
    } else if (Include.EXCLUSIVELY == includeArchived) {
      filters.add(cb.equal(root.get("archived"), true));
    } // else Include.YES, which means don't filter at all

    // Unassessed software shows no level, attention or fragility, so these filters leave it out
    if (level != null) {
      // The override, where there is one, is the level that applies
      filters.add(
          cb.equal(cb.coalesce(root.get("controlLevelOverride"), root.get("controlLevel")), level));
      filters.add(cb.isNotNull(root.get(ASSESSED_DATE)));
    }

    if (attention != null) {
      filters.add(cb.equal(root.get("attention"), attention));
      filters.add(cb.isNotNull(root.get(ASSESSED_DATE)));
    }

    if (fragile) {
      filters.add(root.get("fragility").in(Arrays.asList(Fragility.High, Fragility.Fragile)));
      filters.add(cb.isNotNull(root.get(ASSESSED_DATE)));
    }

    if (assessed != null) {
      filters.add(
          assessed ? cb.isNotNull(root.get(ASSESSED_DATE)) : cb.isNull(root.get(ASSESSED_DATE)));
    }

    if (gapElementId != null) {
      // An element recorded as a gap, among those the software's level requires or recommends:
      // one recorded at another level is not on the checklist now
      Subquery<String> subquery = cq.subquery(String.class);
      Root<SoftwareElement> recorded = subquery.from(SoftwareElement.class);
      Root<SqaElement> element = subquery.from(SqaElement.class);
      Expression<ControlLevel> effective =
          cb.coalesce(root.get("controlLevelOverride"), root.get("controlLevel"));

      List<Predicate> onChecklist = new ArrayList<>();
      for (ControlLevel controlLevel : ControlLevel.values()) {
        onChecklist.add(
            cb.and(
                cb.equal(effective, controlLevel),
                cb.isNotNull(element.get("level" + controlLevel.getValue()))));
      }

      List<Predicate> gap = new ArrayList<>();
      gap.add(cb.equal(recorded.get("id").get(SOFTWARE_ID), root.get(SOFTWARE_ID)));
      gap.add(cb.equal(recorded.get("status"), ElementStatus.GAP));
      gap.add(cb.equal(element.get(ELEMENT_ID), recorded.get("id").get(ELEMENT_ID)));
      gap.add(cb.or(onChecklist.toArray(new Predicate[] {})));
      if (!SqaElementService.ANY_ELEMENT.equals(gapElementId)) {
        gap.add(cb.equal(element.get(ELEMENT_ID), gapElementId));
      }

      subquery.select(recorded.get("id").get(ELEMENT_ID));
      subquery.where(cb.and(gap.toArray(new Predicate[] {})));
      filters.add(cb.exists(subquery));
      filters.add(cb.isNotNull(root.get(ASSESSED_DATE)));
    }

    return filters;
  }

  @PermitAll
  public long countList(
      String softwareName,
      String username,
      Repository repository,
      Workflow workflow,
      boolean noWorkflow,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed,
      String gapElementId) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Software> root = cq.from(Software.class);

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            softwareName,
            username,
            repository,
            workflow,
            noWorkflow,
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed,
            gapElementId);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
