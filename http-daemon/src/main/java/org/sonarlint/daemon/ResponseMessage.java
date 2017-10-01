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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.sonar.api.utils.text.JsonWriter;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon.Issue;
import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon.RuleDetails;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseMessage {

  private final String language;
  private final String languageVersion;
  private final String storedAs;
  private final String code;
  private final List<Issue> issues;
  private final List<RuleDetails> rules;

  public ResponseMessage(String language, String languageVersion, String storedAs, String code, List<Issue> issues, List<RuleDetails> rules) {
    this.language = language;
    this.languageVersion = languageVersion;
    this.storedAs = storedAs;
    this.code = code;
    this.issues = issues;
    this.rules = rules;
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
    AtomicReference<String> line = new AtomicReference("");

    List<CodePiece> pieces = new Underliner(code).underline(issues);

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
    json.prop("total", issues.size());
    json.prop("p", 1);
    json.prop("ps", issues.size());
    json.name("paging")
      .beginObject()
      .prop("pageIndex", 1)
      .prop("pageSize", issues.size())
      .prop("total", issues.size())
      .endObject();
  }

  private void writeRules(JsonWriter json) {
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
    for (Issue issue : issues) {
      json.beginObject();
      json.prop("rule", issue.getRuleKey());
      json.prop("severity", issue.getSeverity().name());
      json.prop("message", issue.getMessage());
      RuleDetails rule = rules.stream().filter(r -> r.getKey().equals(issue.getRuleKey())).findFirst()
        .orElseThrow(() -> new RuntimeException("Cannot find rule "+issue.getRuleKey()));
      json.prop("type", rule.getType());

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
