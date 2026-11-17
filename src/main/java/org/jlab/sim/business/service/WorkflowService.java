package org.jlab.sim.business.service;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import java.util.List;
import org.jlab.sim.persistence.entity.Workflow;
import org.jlab.smoothness.business.service.JPAService;

/** The workflows software is released with. */
@Stateless
public class WorkflowService extends JPAService<Workflow> {
  public WorkflowService() {
    super(Workflow.class);
  }

  /** The workflows, in the order the lists show them. */
  @PermitAll
  public List<Workflow> getAll() {
    return getEntityManager()
        .createQuery("select w from Workflow w order by w.weight, w.name", Workflow.class)
        .getResultList();
  }
}
