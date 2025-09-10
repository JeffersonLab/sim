package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;

@Entity
@Table(name = "SOFTWARE", schema = "SIM_OWNER")
public class Software implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SoftwareId", sequenceName = "SOFTWARE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SoftwareId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SOFTWARE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger softwareId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NAME", nullable = false, length = 128)
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "TYPE", nullable = false, length = 64)
  @Enumerated(EnumType.STRING)
  private SoftwareType type;

  @Size(max = 256)
  @Column(name = "DESCRIPTION", nullable = true, length = 256)
  private String description;

  @Size(max = 256)
  @Column(name = "NOTE", nullable = true, length = 256)
  private String note;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "REPOSITORY_ID", referencedColumnName = "REPOSITORY_ID", nullable = false)
  private Repository repository;

  @Size(max = 256)
  @Column(name = "HOME_URL", nullable = true, length = 256)
  private String homeUrl;

  @Size(max = 256)
  @Column(name = "MAINTAINER_USERNAME_CSV", nullable = true, length = 256)
  private String maintainerUsernameCsv;

  @Basic
  @Column(name = "ARCHIVED_YN", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean archived;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "SOFTWARE_ID",
      referencedColumnName = "SOFTWARE_ID",
      insertable = false,
      updatable = false)
  private List<SoftwareTopic> softwareTopicList;

  @Transient private List<String> stringTopicList;
  @Transient private boolean archivedSynced = false;

  public Software() {}

  public Software(
      Repository repository,
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived,
      String note) {
    this.repository = repository;
    this.name = name;
    this.type = type;
    this.description = description;
    this.maintainerUsernameCsv = maintainerUsernameCsv;
    this.homeUrl = homeUrl;
    this.archived = archived;
    this.note = note;
  }

  public BigInteger getSoftwareId() {
    return softwareId;
  }

  public void setSoftwareId(BigInteger softwareId) {
    this.softwareId = softwareId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SoftwareType getType() {
    return type;
  }

  public void setType(SoftwareType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public void setHomeUrl(String homeUrl) {
    this.homeUrl = homeUrl;
  }

  public String getMaintainerUsernameCsv() {
    return maintainerUsernameCsv;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setMaintainerUsernameCsv(String maintainerUsernameCsv) {
    this.maintainerUsernameCsv = maintainerUsernameCsv;
  }

  @PostLoad
  public void sortSoftwareTopicList() {
    if (softwareTopicList != null) {
      Collections.sort(softwareTopicList);
    }
  }

  public List<SoftwareTopic> getSoftwareTopicList() {
    return softwareTopicList;
  }

  public void setSoftwareTopicList(List<SoftwareTopic> softwareTopicList) {
    this.softwareTopicList = softwareTopicList;
  }

  public String getTopicCsv() {
    String csv = "";
    if (softwareTopicList != null) {
      for (int i = 0; i < softwareTopicList.size(); i++) {
        SoftwareTopic topic = softwareTopicList.get(i);
        csv = csv + topic.getTopic().getName();
        if (i < softwareTopicList.size() - 1) {
          csv = csv + ",";
        }
      }
    }
    return csv;
  }

  public boolean isArchivedSynced() {
    return archivedSynced;
  }

  public void setArchivedSynced(boolean archivedSynced) {
    this.archivedSynced = archivedSynced;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Software)) return false;
    Software software = (Software) o;
    return Objects.equals(softwareId, software.softwareId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(softwareId);
  }

  public boolean syncEquals(Software other) {
    boolean equals = Objects.equals(name, other.name);

    // Only compare if other is non-empty.
    if (other.description != null && !other.description.isBlank()) {
      equals = equals && Objects.equals(description, other.description);
    }

    // Only compare if other is non-empty.
    String otherTopicCsv = other.getStringTopicCsv();
    if (otherTopicCsv != null && !otherTopicCsv.isBlank()) {
      equals = equals && Objects.equals(getTopicCsv(), otherTopicCsv);
    }

    // Only compare if other is non-empty.
    if (other.homeUrl != null && !other.homeUrl.isBlank()) {
      equals = equals && Objects.equals(homeUrl, other.homeUrl);
    }

    // Only compare if other is synced.
    if (other.archivedSynced) {
      equals = equals && Objects.equals(archived, other.archived);
    }

    /*if (other.maintainerUsernameCsv != null && !other.maintainerUsernameCsv.isBlank()) {
      equals = equals && Objects.equals(maintainerUsernameCsv, other.maintainerUsernameCsv);
    }*/

    return equals;
  }

  public List<String> getStringTopicList() {
    return stringTopicList;
  }

  public void setStringTopicList(List<String> stringTopicList) {
    this.stringTopicList = stringTopicList;
  }

  public String getStringTopicCsv() {
    String csv = "";
    if (stringTopicList != null) {
      Collections.sort(stringTopicList);
      for (int i = 0; i < stringTopicList.size(); i++) {
        String topic = stringTopicList.get(i);
        csv = csv + topic;
        if (i < stringTopicList.size() - 1) {
          csv = csv + ",";
        }
      }
    }
    return csv;
  }
}
