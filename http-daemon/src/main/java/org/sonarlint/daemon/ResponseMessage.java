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

import io.instalint.core.AnalyzerResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.sonar.api.utils.text.JsonWriter;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseMessage {

  private final String language;
  private final String languageVersion;
  private final String storedAs;
  private final String code;
  private final AnalyzerResult analyzerResult;

  public ResponseMessage(String language, String languageVersion, String storedAs, String code, AnalyzerResult analyzerResult) {
    this.language = language;
    this.languageVersion = languageVersion;
    this.storedAs = storedAs;
    this.code = code;
    this.analyzerResult = analyzerResult;
  }

  public void writeTo(HttpServletResponse resp) throws IOException {
    try (ServletOutputStream outputStream = resp.getOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8);
         JsonWriter json = JsonWriter.of(writer)) {
      json.beginObject();
      writeLines(json);
      writePagination(json);
      writeIssues(json);
      writeRules(json);
      writeStore(json);
      json.endObject();
    }
    resp.setStatus(200);
  }

  private void writeLines(JsonWriter json) {
    json.name("lines");
    json.beginArray();
    AtomicInteger i = new AtomicInteger();
    AtomicReference<String> line = new AtomicReference<>("");

    List<CodePiece> pieces = new Underliner(code).underline(analyzerResult.issues());

    pieces.forEach(piece -> {
      if (piece.getType() == PieceType.LINE_START) {
        json.beginObject();
        json.prop("line", i.getAndIncrement());
      } else if (piece.getType() == PieceType.UNDERLINE_START) {
        line.getAndUpdate(l -> l + "<span class=\"source-line-code-issue\">");
      } else if (piece.getType() == PieceType.TEXT) {
        line.getAndUpdate(l -> l + escapeHTML(piece.getText()));
      } else if (piece.getType() == PieceType.UNDERLINE_END) {
        line.getAndUpdate(l -> l + "</span>");
      } else if (piece.getType() == PieceType.LINE_END) {
        json.prop("code", line.getAndSet(""));
        json.endObject();
      }
    });
    json.endArray();

    json.prop("code", code);
  }

  public static String escapeHTML(String s) {
    StringBuilder out = new StringBuilder(Math.max(16, s.length()));
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
        out.append("&#");
        out.append((int) c);
        out.append(';');
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  private void writePagination(JsonWriter json) {
    int issueCount = analyzerResult.issues().size();
    json.prop("total", issueCount);
    json.prop("p", 1);
    json.prop("ps", issueCount);
    json.name("paging")
      .beginObject()
      .prop("pageIndex", 1)
      .prop("pageSize", issueCount)
      .prop("total", issueCount)
      .endObject();
  }

  private void writeRules(JsonWriter json) {
    class RuleDetails {
      private String key;
      private String name;
      private String language;

      private RuleDetails(String key, String name, String language) {
        this.key = key;
        this.name = name;
        this.language = language;
      }

      private String getKey() {
        return key;
      }

      private String getName() {
        return name;
      }

      private String getLanguage() {
        return language;
      }
    }
    List<RuleDetails> rules = new ArrayList<>();
    Set<String> seen = new HashSet<>();

    for (Issue issue : analyzerResult.issues()) {
      String ruleKey = issue.getRuleKey();
      if (seen.add(ruleKey)) {
        rules.add(new RuleDetails(ruleKey, issue.getRuleName(), language));
      }
    }

    // TODO what are these used for? are they necessary?
    json.name("rules");
    json.beginArray();
    for (RuleDetails rule : rules) {
      json.beginObject();
      json.prop("key", rule.getKey());
      json.prop("name", rule.getName());
      json.prop("lang", rule.getLanguage());
      json.prop("langName", rule.getLanguage());
      json.endObject();
    }
    json.endArray();
  }

  private void writeIssues(JsonWriter json) {
    json.name("issues");
    json.beginArray();
    for (Issue issue : analyzerResult.issues()) {
      json.beginObject();
      json.prop("rule", issue.getRuleKey());
      json.prop("severity", issue.getSeverity());
      json.prop("message", issue.getMessage());
      json.prop("type", issue.getType());

      json.name("textRange")
        .beginObject()
        .prop("startLine", issue.getStartLine())
        .prop("endLine", issue.getEndLine())
        .prop("startOffset", issue.getStartLineOffset())
        .prop("endOffset", issue.getEndLineOffset())
        .endObject();

      json.endObject();
    }
    json.endArray();
  }

  private void writeStore(JsonWriter json) {
    if (languageVersion != null) {
      json.prop("languageVersion", languageVersion);
    }
    if (storedAs != null) {
      json.prop("storedAs", storedAs);
    }
  }
}
