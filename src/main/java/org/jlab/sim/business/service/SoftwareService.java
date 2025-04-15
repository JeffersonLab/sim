package org.jlab.sim.business.service;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  public SoftwareService() {
    super(Software.class);
  }

  @PermitAll
  public void addSoftware(
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl)
      throws UserFriendlyException {
    checkAuthenticated();

    if (name == null || name.isEmpty()) {
      throw new UserFriendlyException("Name cannot be empty");
    }

    if (type == null) {
      throw new UserFriendlyException("Type cannot be empty");
    }

    Software software = new Software(name, type, description, maintainerUsernameCsv, homeUrl);

    create(software);
  }

  @PermitAll
  public List<Software> findAll(OrderDirective... directives) {
    return super.findAll(directives);
  }
}
