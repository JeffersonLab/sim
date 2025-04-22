package org.jlab.sim.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.sim.business.service.RepositoryService;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.business.service.SyncService;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.view.SoftwareDiff;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "Sync",
    urlPatterns = {"/repositories/sync"})
public class Sync extends HttpServlet {

  @EJB RepositoryService repositoryService;
  @EJB SyncService syncService;
  @EJB SoftwareService softwareService;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String error = null;
    Repository repository = null;
    List<Software> localList = null;
    List<Software> remoteList = null;
    SoftwareDiff diff = null;
    Map<String, Software> remoteMap = new HashMap<>();

    try {
      BigInteger repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");

      repository = repositoryService.find(repositoryId);

      remoteList = syncService.fetch(repository);

      for (Software software : remoteList) {
        remoteMap.put(software.getName(), software);
      }

      localList =
          softwareService.filterList(
              null, null, repository, null, null, Include.YES, 0, Integer.MAX_VALUE);

      diff = syncService.diff(localList, remoteMap);
    } catch (UserFriendlyException e) {
      e.printStackTrace();
      error = e.getMessage();
    }

    request.setAttribute("diff", diff);
    request.setAttribute("localList", localList);
    request.setAttribute("remoteList", remoteList);
    request.setAttribute("remoteMap", remoteMap);
    request.setAttribute("error", error);
    request.setAttribute("repository", repository);

    request.getRequestDispatcher("/WEB-INF/views/sync.jsp").forward(request, response);
  }
}
