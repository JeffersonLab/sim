package org.jlab.sim.business.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  @EJB RepositoryService repositoryService;
  @EJB SoftwareTopicService softwareTopicService;

  public SoftwareService() {
    super(Software.class);
  }

  @PermitAll
  public void addSoftware(
      BigInteger repoId,
      String name,
      SoftwareType type,
      String[] topicArray,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived)
      throws UserFriendlyException {
    checkAuthenticated();

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
        new Software(repo, name, type, description, maintainerUsernameCsv, homeUrl, archived);

    create(software);

    em.flush();

    softwareTopicService.set(software, topicArray);
  }

  @PermitAll
  public void editSoftware(
      BigInteger softwareId,
      BigInteger repoId,
      SoftwareType type,
      String[] topicArray,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived)
      throws UserFriendlyException {
    checkAuthenticated();

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

    edit(software);

    em.flush();

    softwareTopicService.set(software, topicArray);
  }

  @PermitAll
  public void removeSoftware(BigInteger softwareId) throws UserFriendlyException {
    checkAuthenticated();

    if (softwareId == null) {
      throw new UserFriendlyException("softwareId cannot be empty");
    }

    Software software = find(softwareId);

    if (software == null) {
      throw new UserFriendlyException("software not found with id: " + softwareId);
    }

    remove(software);
  }

  @PermitAll
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
            includeArchived);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
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
      Include includeArchived) {
    List<Predicate> filters = new ArrayList<>();

    if (softwareName != null && !softwareName.isEmpty()) {
      softwareName = softwareName.replaceAll("\\*", "%");
      filters.add(cb.like(cb.lower(root.get("name")), softwareName.toLowerCase()));
    }

    if (username != null && !username.isEmpty()) {
      username = username.replaceAll("\\*", "%");
      username = "%" + username + "%";
      filters.add(cb.like(cb.lower(root.get("maintainerUsernameCsv")), username.toLowerCase()));
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

    return filters;
  }

  @PermitAll
  public long countList(
      String softwareName,
      String username,
      Repository repository,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived) {
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
            includeArchived);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
