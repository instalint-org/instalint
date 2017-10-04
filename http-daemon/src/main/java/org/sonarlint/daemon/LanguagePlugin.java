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

import java.net.URL;

public class LanguagePlugin {
  private final URL url;
  private final String languageVersion;
  private final String inputFileExtension;

  public LanguagePlugin(URL url, String languageVersion, String inputFileExtension) {
    this.url = url;
    this.languageVersion = languageVersion;
    this.inputFileExtension = inputFileExtension;
  }

  public URL getUrl() {
    return url;
  }

  public String getLanguageVersion() {
    return languageVersion;
  }

  public String getInputFileExtension() {
    return inputFileExtension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    LanguagePlugin that = (LanguagePlugin) o;

    return url != null ? url.equals(that.url) : that.url == null;
  }

  @Override
  public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }
}
