package org.jlab.sim.presentation.controller.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.sim.business.service.SqaElementService;
import org.jlab.sim.persistence.enumeration.ElementStatus;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.presentation.util.ParamConverter;

/**
 * Records a product's SQA elements. The elements come as four lists of the same length, as jQuery
 * posts arrays: elementId[], status[] (blank clears what was recorded), link[] and note[].
 */
@WebServlet(
    name = "EditSoftwareElements",
    urlPatterns = {"/ajax/edit-software-elements"})
// false positive on @EJB lines (Injected EJB proxy IS Serializable)
@SuppressWarnings({"PMD.NonSerializableClass"})
public class EditSoftwareElements extends HttpServlet {

  @Serial private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(EditSoftwareElements.class.getName());
  private static final String FAIL = "fail";
  private static final String PREFIX = "Unable to record SQA Elements";

  @EJB SqaElementService elementService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;

    try {
      BigInteger softwareId = ParamConverter.convertBigInteger(request, "softwareId");

      String[] ids = values(request, "elementId[]");
      String[] statuses = values(request, "status[]");
      String[] links = values(request, "link[]");
      String[] notes = values(request, "note[]");

      if (statuses.length != ids.length
          || links.length != ids.length
          || notes.length != ids.length) {
        throw new UserFriendlyException("Each element needs a status, a link and a note");
      }

      List<SqaElementService.Entry> entries = new ArrayList<>();
      for (int i = 0; i < ids.length; i++) {
        ElementStatus status = statuses[i].isBlank() ? null : ElementStatus.valueOf(statuses[i]);
        entries.add(new SqaElementService.Entry(ids[i], status, links[i], notes[i]));
      }

      elementService.record(softwareId, entries, request.getRemoteUser());
    } catch (UserFriendlyException e) {
      stat = FAIL;
      error = PREFIX + ": " + e.getUserMessage();
    } catch (EJBAccessException e) {
      stat = FAIL;
      error = PREFIX + ": Not authenticated / authorized (do you need to re-login?)";
    } catch (IllegalArgumentException e) {
      // Such as an id that isn't a number, or a status that isn't one
      stat = FAIL;
      error = PREFIX + ": " + e.getMessage();
    } catch (RuntimeException e) {
      stat = FAIL;
      error = PREFIX;
      logger.log(Level.SEVERE, PREFIX, e);
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

  private static String[] values(HttpServletRequest request, String name) {
    String[] values = request.getParameterValues(name);
    return values == null ? new String[0] : values;
  }
}
