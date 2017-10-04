/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.daemon;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

public class LanguagePluginRepository {

  private Path pluginDir;

  public void init(Path workDir) {
    if (pluginDir == null) {
      pluginDir = workDir.resolve("plugins");
      Logger.getLogger(Backend.class.getName()).warning(() -> "pluginDir: " + pluginDir.toAbsolutePath());
    }
  }

  public LanguagePlugin retrieve(String language, String languageVersion, Properties properties) {
    if ("latest".equals(languageVersion)) {
      languageVersion = properties.getProperty(language.toLowerCase(Locale.ENGLISH) + ".latestVersion");
    }
    String fileProperty = properties.getProperty(language.toLowerCase(Locale.ENGLISH) + ".plugin." + languageVersion);
    Path plugin = pluginDir.resolve(fileProperty);
    URL url;
    try {
      url = plugin.toFile().toURI().toURL();
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Cannot locate language plugin", e);
    }
    return new LanguagePlugin(url, languageVersion);
  }
}
