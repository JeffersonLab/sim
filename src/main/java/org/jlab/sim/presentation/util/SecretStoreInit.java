package org.jlab.sim.presentation.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.jlab.sim.business.service.SecretStore;

@WebListener
public class SecretStoreInit implements ServletContextListener {
  private static final Logger LOGGER = Logger.getLogger(SecretStoreInit.class.getName());

  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      SecretStore.init();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error initializing secret store", e);
    }
  }
}
