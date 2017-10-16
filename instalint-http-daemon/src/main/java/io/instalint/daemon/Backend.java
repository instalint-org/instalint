package io.instalint.daemon;

import io.instalint.core.Analyzer;
import io.instalint.core.AnalyzerId;
import io.instalint.core.LanguagePlugin;
import io.instalint.core.LanguagePluginRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

class Backend {

  private static final Logger LOGGER = Logger.getLogger(Backend.class.getName());

  private Path workDir;
  private Properties properties;
  private SourceRepository sourceRepository;
  private LanguagePluginRepository languagePluginRepository;
  private Map<AnalyzerId, Analyzer> analyzerRepository = new HashMap<>();

  private void init() {
    if (workDir == null) {
      String catalinaBase = System.getProperty("catalina.base");
      workDir = Paths.get(catalinaBase, "work", "Catalina", "localhost", "ROOT");
      LOGGER.warning(() -> "workDir: " + workDir.toAbsolutePath());
    }

    if (properties == null) {
      Path settingsFile = workDir.resolve("settings.properties");
      if (!settingsFile.toFile().exists()) {
        String message = "Settings file not found: " + settingsFile.toAbsolutePath();
        LOGGER.severe(() -> message);
        throw new IllegalStateException(message);
      }

      Properties settings = new Properties();
      try {
        settings.load(new FileInputStream(settingsFile.toFile()));
      } catch (IOException e) {
        String message = "Error while reading settings file: " + settingsFile.toAbsolutePath();
        LOGGER.severe(() -> message);
        throw new IllegalStateException(message);
      }
      this.properties = settings;
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

  Analyzer retrieve(String language, String languageVersion) {
    AnalyzerId id = AnalyzerId.of(language, languageVersion);
    Analyzer analyzer = analyzerRepository.get(id);
    if (analyzer == null) {
      init();
      LanguagePlugin languagePlugin = languagePluginRepository.retrieve(language, languageVersion, properties);
      analyzer = new Analyzer(languagePlugin);
      analyzerRepository.put(id, analyzer);
    }
    return analyzer;
  }

  String load(String uuid) {
    init();
    return sourceRepository.load(uuid);
  }

  String store(String code) {
    init();
    return sourceRepository.store(code);
  }
}
