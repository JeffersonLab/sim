package org.jlab.sim.persistence.view;

import java.util.ArrayList;
import java.util.List;
import org.jlab.sim.persistence.entity.Software;

public class SoftwareDiff {
  public final List<Software> removeList = new ArrayList<>();
  public final List<Software> addList = new ArrayList<>();
  public final List<Software> updateList = new ArrayList<>();
  public final List<Software> matchList = new ArrayList<>();

  public List<Software> getRemoveList() {
    return removeList;
  }

  public List<Software> getAddList() {
    return addList;
  }

  public List<Software> getUpdateList() {
    return updateList;
  }

  public List<Software> getMatchList() {
    return matchList;
  }

  public boolean hasChanges() {
    return removeList.size() > 0 || addList.size() > 0 || updateList.size() > 0;
  }

  public int getMatchCount() {
    return matchList.size();
  }

  public int getAddCount() {
    return addList.size();
  }

  public int getRemoveCount() {
    return removeList.size();
  }

  public int getUpdateCount() {
    return updateList.size();
  }
}
