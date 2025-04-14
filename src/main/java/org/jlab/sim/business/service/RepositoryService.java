package org.jlab.sim.business.service;

import javax.ejb.Stateless;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class RepositoryService extends JPAService<Repository> {
  public RepositoryService() {
    super(Repository.class);
  }
}
