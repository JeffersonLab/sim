package org.jlab.sim.business.service;

import javax.ejb.Stateless;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareService extends JPAService<Software> {
  public SoftwareService() {
    super(Software.class);
  }
}
