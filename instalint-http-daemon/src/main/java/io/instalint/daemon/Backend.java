package io.instalint.daemon;

import io.instalint.core.Analyzer;
import io.instalint.core.AnalyzerId;
import io.instalint.core.LanguagePlugin;
import io.instalint.core.LanguagePluginRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
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
      Path propertyFile = workDir.resolve("settings.properties");
      if (!propertyFile.toFile().exists()) {
        LOGGER.severe(() -> "Property file not found: " + propertyFile.toAbsolutePath());
        throw new IllegalStateException("Property file not found");
      }
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream(propertyFile.toFile()));
      } catch (IOException e) {
        LOGGER.severe(() -> "Property file not found: " + propertyFile.toAbsolutePath());
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
