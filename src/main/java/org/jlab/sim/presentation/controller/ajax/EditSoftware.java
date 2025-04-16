package org.jlab.sim.presentation.controller.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.jlab.sim.presentation.util.Parameter;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

@WebServlet(
    name = "EditSoftware",
    urlPatterns = {"/ajax/edit-software"})
public class EditSoftware extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditSoftware.class.getName());

  @EJB SoftwareService softwareService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;
    String name = null;

    try {
      BigInteger softwareId = ParamConverter.convertBigInteger(request, "softwareId");
      BigInteger repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");
      String description = request.getParameter("description");
      String maintainerUsernameCsv = request.getParameter("maintainerUsernameCsv");
      String homeUrl = request.getParameter("homeUrl");
      SoftwareType type = Parameter.convertSoftwareType(request, "type");
      Boolean archived = ParamConverter.convertYNBoolean(request, "archived");

      if (archived == null) {
        throw new UserFriendlyException("archived must not be empty");
      }

      // Since name is key in other repos, must delete and add new for name changes.
      softwareService.editSoftware(
          softwareId, repositoryId, type, description, maintainerUsernameCsv, homeUrl, archived);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Software: " + e.getUserMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Software: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Software";
      logger.log(Level.SEVERE, "Unable to edit Software", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      if ("OracleDatabaseException".equals(rootCause.getClass().getSimpleName())) {
        error = "Oracle Database Exception - make sure name doesn't already exist: " + name;
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
}
