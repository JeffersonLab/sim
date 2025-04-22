package org.jlab.sim.business.service;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class TopicService extends JPAService<Topic> {
  public TopicService() {
    super(Topic.class);
  }

  @PermitAll
  public List<Topic> getAll() {
    // We need to update smoothness weblib findAll to PermitAll!
    return findAll(new JPAService.OrderDirective("name", true));
  }

  @PermitAll
  public Topic findByName(String topicName) {
    TypedQuery<Topic> query =
        getEntityManager()
            .createQuery("select t from Topic t where lower(t.name) = lower(:name)", Topic.class);

    query.setParameter("name", topicName);

    return query.getSingleResult();
  }
}
