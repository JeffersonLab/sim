package org.jlab.sim.business.service;

import javax.ejb.Stateless;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class TopicService extends JPAService<Topic> {
  public TopicService() {
    super(Topic.class);
  }
}
