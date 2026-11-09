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
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.enumeration.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  @EJB RepositoryService repositoryService;
  @EJB SoftwareTopicService softwareTopicService;

  static final String ACG_ROLE = "acg";
  static final String ADMIN_ROLE = "sim-admin";
  private static final String ASSESSED_DATE = "assessedDate";

  public SoftwareService() {
    super(Software.class);
  }

  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void addSoftware(
      BigInteger repoId,
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

    create(software);

    em.flush();

    softwareTopicService.set(software, topicArray);
  }

  @RolesAllowed({ADMIN_ROLE, ACG_ROLE})
  public void editSoftware(
      BigInteger softwareId,
      BigInteger repoId,
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

  @PermitAll
  public List<Software> filterList(
      String softwareName,
      String username,
      Repository repository,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed,
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
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path<String> p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
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
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed) {
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

    if (type != null) {
      filters.add(cb.equal(root.get("type"), type));
    }

    if (topicNameArray != null && topicNameArray.length > 0) {
      for (String topicName : topicNameArray) {
        Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
        Root<SoftwareTopic> subqueryRoot = subquery.from(SoftwareTopic.class);
        subquery.select(subqueryRoot.get("software").get("softwareId"));
        subquery.where(cb.equal(subqueryRoot.get("topic").get("name"), topicName));
        filters.add(cb.in(root.get("softwareId")).value(subquery));
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

    return filters;
  }

  @PermitAll
  public long countList(
      String softwareName,
      String username,
      Repository repository,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed) {
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
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
