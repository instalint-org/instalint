/*
 * SonarLint Daemon
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

import io.instalint.core.AnalyzerExecutor;
import io.instalint.core.AnalyzerResult;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnalyzerServlet extends HttpServlet {

  private Backend backend;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (backend == null) {
      backend = new Backend();
    }

    String storedAs = req.getParameter("restore");
    String code;
    if (storedAs != null && !storedAs.isEmpty()) {
      code = backend.load(storedAs);
    } else {
      code = req.getParameter("code");
      if (code == null || code.isEmpty()) {
        throw new IllegalStateException("Parameter code is required");
      }
    }

    String languageParam = getLanguage(req);
    String languageVersionParam = getLanguageVersion(req);
    LanguagePlugin languagePlugin = backend.retrieve(languageParam, languageVersionParam);

    if ("true".equals(req.getParameter("store"))) {
      storedAs = backend.store(code);
    }

    AnalyzerExecutor executor = new AnalyzerExecutor() {
      @Override
      public AnalyzerResult execute(LanguagePlugin languagePlugin, String code) {
        return null;
      }
    };
    AnalyzerResult result = executor.execute(languagePlugin, code);

    new ResponseMessage(languageParam, languagePlugin.getLanguageVersion(), storedAs, code, result).writeTo(resp);
  }

  private String getLanguageVersion(HttpServletRequest req) {
    String languageVersion = req.getParameter("languageVersion");
    if (languageVersion.isEmpty()) {
      languageVersion = null;
    }
    if (languageVersion != null && !languageVersion.matches("^[a-zA-Z0-9\\.]*$")) {
      throw new IllegalStateException("Invalid language version");
    }
    return languageVersion;
  }

  private String getLanguage(HttpServletRequest req) {
    String language = req.getParameter("language");
    if (language == null || language.isEmpty()) {
      throw new IllegalStateException("Parameter language is required");
    }
    return language;
  }
}
