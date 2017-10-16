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
package io.instalint.core;

public class AnalyzerId {

  private final String language;
  private final String languageVersion;

  public AnalyzerId(String language, String languageVersion) {
    this.language = language;
    this.languageVersion = languageVersion;
  }

  public static AnalyzerId of(String language, String languageVersion) {
    return new AnalyzerId(language, languageVersion);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AnalyzerId that = (AnalyzerId) o;

    if (language != null ? !language.equals(that.language) : that.language != null) return false;
    return languageVersion != null ? languageVersion.equals(that.languageVersion) : that.languageVersion == null;
  }

  @Override
  public int hashCode() {
    int result = language != null ? language.hashCode() : 0;
    result = 31 * result + (languageVersion != null ? languageVersion.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AnalyzerId{" +
      "language='" + language + '\'' +
      ", languageVersion='" + languageVersion + '\'' +
      '}';
  }
}
