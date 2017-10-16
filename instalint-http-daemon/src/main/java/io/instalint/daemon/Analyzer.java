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
package io.instalint.daemon;

import io.instalint.core.AnalyzerExecutor;
import io.instalint.core.AnalyzerExecutorImpl;
import io.instalint.core.AnalyzerResult;
import io.instalint.core.LanguagePlugin;

public class Analyzer {

  private final LanguagePlugin languagePlugin;
  private final AnalyzerExecutor executor = new AnalyzerExecutorImpl();

  public Analyzer(LanguagePlugin languagePlugin) {
    this.languagePlugin = languagePlugin;
  }

  public AnalyzerResult apply(String code) {
    return executor.execute(languagePlugin, code);
  }

  public String getLanguageVersion() {
    return languagePlugin.getLanguageVersion();
  }
}