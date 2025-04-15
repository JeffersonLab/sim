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
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  @EJB RepositoryService repositoryService;

  public SoftwareService() {
    super(Software.class);
  }

  @PermitAll
  public void addSoftware(
      BigInteger repoId,
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl)
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

    Software software = new Software(repo, name, type, description, maintainerUsernameCsv, homeUrl);

    create(software);
  }

  @PermitAll
  public void editSoftware(
      BigInteger softwareId,
      BigInteger repoId,
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl)
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

    if (name == null || name.isEmpty()) {
      throw new UserFriendlyException("Name cannot be empty");
    }

    if (type == null) {
      throw new UserFriendlyException("Type cannot be empty");
    }

    software.setName(name);
    software.setType(type);
    software.setDescription(description);
    software.setMaintainerUsernameCsv(maintainerUsernameCsv);
    software.setHomeUrl(homeUrl);

    edit(software);
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
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Software> cq = cb.createQuery(Software.class);
    Root<Software> root = cq.from(Software.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, cq, root, softwareName, username, repository, type);

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
      SoftwareType type) {
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

    return filters;
  }

  @PermitAll
  public long countList(
      String softwareName, String username, Repository repository, SoftwareType type) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Software> root = cq.from(Software.class);

    List<Predicate> filters = getFilters(cb, cq, root, softwareName, username, repository, type);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
