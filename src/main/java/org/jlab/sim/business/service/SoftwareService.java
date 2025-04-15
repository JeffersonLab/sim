package org.jlab.sim.business.service;

import java.math.BigInteger;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
  public List<Software> findAll(OrderDirective... directives) {
    return super.findAll(directives);
  }
}
