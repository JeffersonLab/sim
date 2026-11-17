package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * An SQA element a repository provides for all its software, such as version control for every
 * project on GitLab; see 02_ddl.sql. The rows are kept in the database, and are read only here.
 */
@Entity
@Table(name = "REPOSITORY_ELEMENT", schema = "SIM_OWNER")
public class RepositoryElement implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId private RepositoryElementId id;

  /** On what basis the repository provides the element */
  @Column(name = "NOTE", nullable = false, length = 512)
  private String note;

  @Column(name = "LINK_URL", length = 512)
  private String linkUrl;

  public RepositoryElement() {}

  public BigInteger getRepositoryId() {
    return id.getRepositoryId();
  }

  public String getElementId() {
    return id.getElementId();
  }

  public String getNote() {
    return note;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RepositoryElement)) return false;
    RepositoryElement that = (RepositoryElement) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
