package io.instalint.daemon;

import io.instalint.core.LanguagePlugin;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

public class Backend {

  private Path workDir;
  private Properties properties;
  private SourceRepository sourceRepository;
  private LanguagePluginRepository languagePluginRepository;

  private void init() {
    if (workDir == null) {
      String catalinaBase = System.getProperty("catalina.base");
      workDir = Paths.get(catalinaBase, "work", "Catalina", "localhost", "ROOT");
      Logger.getLogger(Backend.class.getName()).warning(() -> "workDir: " + workDir.toAbsolutePath());
    }

    if (properties == null) {
      Path propertyFile = workDir.resolve("settings.properties");
      if (!Files.exists(propertyFile)) {
        Logger.getLogger(Backend.class.getName()).severe(() -> "Property file not found: " + propertyFile.toAbsolutePath());
        throw new IllegalStateException("Property file not found");
      }
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(propertyFile.toFile()));
      } catch (IOException e) {
        Logger.getLogger(Backend.class.getName()).severe(() -> "Property file not found: " + propertyFile.toAbsolutePath());
        throw new IllegalStateException("Property file not found");
      }
      this.properties = properties;
    }

    if (languagePluginRepository == null) {
      languagePluginRepository = new LanguagePluginRepository();
      languagePluginRepository.init(workDir);
    }

    if (sourceRepository == null) {
      sourceRepository = new SourceRepository();
      sourceRepository.init(workDir);
    }
  }

  public LanguagePlugin retrieve(String language, String languageVersion) {
    init();
    return languagePluginRepository.retrieve(language, languageVersion, properties);
  }

  public String load(String uuid) {
    init();
    return sourceRepository.load(uuid);
  }

  public String store(String code) {
    init();
    return sourceRepository.store(code);
  }
}
