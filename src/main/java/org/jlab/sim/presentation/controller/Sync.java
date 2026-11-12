package org.jlab.sim.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.sim.business.service.RepositoryService;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.business.service.SyncService;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.enumeration.SoftwareOrder;
import org.jlab.sim.persistence.view.SoftwareDiff;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * @author ryans
 */
@WebServlet(
    name = "Sync",
    urlPatterns = {"/repositories/sync"})
// false positive on @EJB lines (Injected EJB proxy IS Serializable)
@SuppressWarnings({"PMD.NonSerializableClass"})
public class Sync extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(Sync.class.getName());

  @Serial private static final long serialVersionUID = 1L;

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

    BigInteger repositoryId;

    try {
      repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    if (repositoryId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Repository repository = repositoryService.find(repositoryId);

    if (repository == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String error = null;
    List<Software> localList = null;
    List<Software> remoteList = null;
    SoftwareDiff diff = null;
    Map<String, Software> remoteMap = new HashMap<>();

    try {
      remoteList = syncService.fetch(repository);

      for (Software software : remoteList) {
        remoteMap.put(software.getName(), software);
      }

      localList =
          softwareService.filterList(
              null,
              null,
              repository,
              null,
              null,
              Include.YES,
              null,
              null,
              false,
              null,
              SoftwareOrder.NAME,
              0,
              Integer.MAX_VALUE);

      diff = syncService.diff(localList, remoteMap);
    } catch (UserFriendlyException e) {
      LOGGER.log(Level.SEVERE, e, e::getMessage);
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
