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

import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sonarlint.daemon.services.StandaloneSonarLintImpl;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon.AnalyzeContentRequest;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon.AnalyzeContentRequest.Builder;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon.Issue;

import static java.util.Collections.singletonList;

public class AnalyzerServlet extends HttpServlet {

  private Map<LanguagePlugin, StandaloneSonarLintImpl> sonarLintRepository = new HashMap<>();
  private Backend backend;

  private StandaloneSonarLintImpl initSonarLint(LanguagePlugin languagePlugin) {
    StandaloneSonarLintImpl sonarLint = sonarLintRepository.get(languagePlugin);
    if (sonarLint == null) {
      try {
        sonarLint = new StandaloneSonarLintImpl(singletonList(languagePlugin.getUrl()));
      } catch (Exception e) {
        throw new IllegalStateException("Cannot initialize analyzers", e);
      }
      sonarLintRepository.put(languagePlugin, sonarLint);
    }
    return sonarLint;
  }

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

    String language = getLanguage(req);
    String languageVersionParam = getLanguageVersion(req);
    LanguagePlugin languagePlugin = backend.retrieve(language, languageVersionParam);
    String languageVersion = languagePlugin.getLanguageVersion();
    StandaloneSonarLintImpl sonarlint = initSonarLint(languagePlugin);

    List<Issue> issues = getIssues(sonarlint, code, language);
    List<SonarlintDaemon.RuleDetails> rules = getRules(sonarlint, issues);

    if ("true".equals(req.getParameter("store"))) {
      storedAs = backend.store(code);
    }

    new ResponseMessage(language, languageVersion, storedAs, code, issues, rules).writeTo(resp);
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

  private List<Issue> getIssues(StandaloneSonarLintImpl sonarlint, String postBody, String language) {
    Builder build = AnalyzeContentRequest.newBuilder();
    build.setCharset("UTF-8");

    build.setContent(postBody);
    build.setLanguage(language);

    List<Issue> issues = new ArrayList<>();
    sonarlint.analyzeContent(build.build(), new StreamObserver<Issue>() {

      @Override
      public void onCompleted() {
      }

      @Override
      public void onError(Throwable arg0) {
      }

      @Override
      public void onNext(Issue arg0) {
        issues.add(arg0);
      }
    });
    return issues;
  }

  private List<SonarlintDaemon.RuleDetails> getRules(StandaloneSonarLintImpl sonarlint, List<Issue> issues) {
    List<SonarlintDaemon.RuleDetails> rules = new ArrayList<>();
    Set<String> ruleKeys = issues.stream().map(Issue::getRuleKey).collect(Collectors.toSet());
    for (String ruleKey : ruleKeys) {
      SonarlintDaemon.RuleKey ruleKeyParsed = SonarlintDaemon.RuleKey.newBuilder().setKey(ruleKey).build();
      sonarlint.getRuleDetails(ruleKeyParsed, new StreamObserver<SonarlintDaemon.RuleDetails>() {
        @Override
        public void onNext(SonarlintDaemon.RuleDetails ruleDetails) {
          rules.add(ruleDetails);
        }

        @Override
        public void onError(Throwable throwable) {
        }

        @Override
        public void onCompleted() {
        }
      });
    }
    return rules;
  }
}
