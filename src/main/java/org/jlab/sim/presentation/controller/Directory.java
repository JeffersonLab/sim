package org.jlab.sim.presentation.controller;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.smoothness.business.service.JPAService;

/**
 * @author ryans
 */
@WebServlet(
    name = "Directory",
    urlPatterns = {"/directory"})
public class Directory extends HttpServlet {

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

    List<Software> softwareList =
        softwareService.findAll(new JPAService.OrderDirective("name", true));

    request.setAttribute("softwareList", softwareList);

    request.getRequestDispatcher("/WEB-INF/views/directory.jsp").forward(request, response);
  }
}
