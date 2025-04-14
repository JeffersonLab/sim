package org.jlab.sim.presentation.controller.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.ExceptionUtil;

@WebServlet(
    name = "AddSoftware",
    urlPatterns = {"/ajax/add-software"})
public class AddAlarm extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AddAlarm.class.getName());

  @EJB SoftwareService softwareService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;

    try {
      String name = request.getParameter("name");
      String description = request.getParameter("description");
      String maintainerUsernameCsv = request.getParameter("maintainerUsernameCsv");
      String homeUrl = request.getParameter("homeUrl");
      SoftwareType type = convertSoftwareType(request, "type");

      softwareService.addSoftware(name, type, description, maintainerUsernameCsv, homeUrl);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to add Software: " + e.getUserMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to add Software: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to add Software";
      logger.log(Level.SEVERE, "Unable to add Software", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      if ("OracleDatabaseException".equals(rootCause.getClass().getSimpleName())) {
        error = "Oracle Database Exception - make sure name doesn't already exist!";
      }
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      if (error != null) {
        gen.write("error", error);
      }
      gen.writeEnd();
    }
  }

  private SoftwareType convertSoftwareType(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    SoftwareType result = null;

    if (value != null) {
      result = SoftwareType.valueOf(value);
    }

    return result;
  }
}
