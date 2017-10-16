package io.instalint.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

public class LanguagePluginRepository {

  private static final Logger LOGGER = Logger.getLogger(LanguagePluginRepository.class.getName());

  private Path pluginDir;

  public void init(Path workDir) {
    if (pluginDir == null) {
      pluginDir = workDir.resolve("plugins");
      LOGGER.warning(() -> "pluginDir: " + pluginDir.toAbsolutePath());
    }
  }

  public LanguagePlugin retrieve(String language, String languageVersion, Properties properties) {
    String languageCode = language.toLowerCase(Locale.ENGLISH);
    if ("latest".equals(languageVersion)) {
      languageVersion = properties.getProperty(languageCode + ".latestVersion");
    }
    String filePropertyName = languageCode + ".plugin." + languageVersion;
    String fileProperty = properties.getProperty(filePropertyName);
    if (fileProperty == null) {
      throw new IllegalArgumentException("Could not find property: " + filePropertyName);
    }
    Path plugin = pluginDir.resolve(fileProperty);
    URL url;
    try {
      url = plugin.toFile().toURI().toURL();
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Cannot locate language plugin", e);
    }
    return new LanguagePlugin(url, languageVersion, InputFileExtensions.fromLanguageCode(languageCode));
  }
}
