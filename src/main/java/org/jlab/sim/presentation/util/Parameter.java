package org.jlab.sim.presentation.util;

import org.jlab.sim.persistence.enumeration.SoftwareType;

import javax.servlet.http.HttpServletRequest;

public class Parameter {
    public static SoftwareType convertSoftwareType(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        SoftwareType result = null;

        if (value != null) {
            result = SoftwareType.valueOf(value);
        }

        return result;
    }
}
