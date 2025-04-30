package org.jlab.sim.business.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.jlab.smoothness.business.service.SettingsService;

public class SecretStore {

  private static Properties props = null;

  // Private access, can't be instantiated
  private SecretStore() {}

  /**
   * Initialize the secrets file. At the moment, you must re-deploy the app to update. This is
   * better than an env where you'd have to restart the entire app server (and ALL apps). In the
   * future we could provide a button or something that re-calls this method on-demand avoiding
   * re-deploy.
   *
   * @throws IOException If unable to read the secrets file
   */
  public static void init() throws IOException {
    props = new Properties();

    String path = SettingsService.cachedSettings.get("SECRET_STORE_FILE_PATH");

    if (path != null) {

      Path p = Paths.get(path);

      if (!p.isAbsolute()) {
        String userHome = System.getProperty("user.home");
        path = Paths.get(userHome, p.toString()).toString();
      }

      try (FileInputStream input = new FileInputStream(path)) {
        props.load(input);
      }
    } else {
      throw new IOException("SECRET_STORE_FILE_PATH not set");
    }
  }

  public static String get(String key) {
    return props.getProperty(key);
  }
}
