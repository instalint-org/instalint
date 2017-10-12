package io.instalint.daemon;

import io.instalint.core.AnalyzerResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.utils.text.JsonWriter;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Highlighting;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseMessage {

  private final String languageVersion;
  private final String storedAs;
  private final String code;
  private final AnalyzerResult analyzerResult;

  private final Pattern parseErrorPattern = Pattern.compile("^Parse error at line (\\d+) column (\\d+):.*", Pattern.DOTALL);

  public ResponseMessage(String languageVersion, String storedAs, String code, AnalyzerResult analyzerResult) {
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
      json.prop("code", code);
      writePagination(json);
      writeIssues(json);
      writeStore(json);
      writeHighlightings(json);
      writeSymbolRefs(json);
      writeErrors(json);
      writeSuccess(json);
      json.endObject();
    }
    resp.setStatus(200);
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

  private void writeHighlightings(JsonWriter json) {
    json.name("highlightings");
    json.beginArray();
    for (Highlighting highlighting : analyzerResult.highlightings()) {
      json.beginObject()
        .prop("type", highlighting.type().name())
        .prop("cssClass", highlighting.type().cssClass())
        .prop("startLine", highlighting.textRange().start().line())
        .prop("endLine", highlighting.textRange().end().line())
        .prop("startOffset", highlighting.textRange().start().lineOffset())
        .prop("endOffset", highlighting.textRange().end().lineOffset())
        .endObject();
    }
    json.endArray();
  }

  private void writeSymbolRefs(JsonWriter json) {
    json.name("symbolRefs");
    json.beginArray();
    for (Map.Entry<TextRange, Set<TextRange>> entry : analyzerResult.symbolRefs().entrySet()) {
      json.beginObject();
      {
        {
          TextRange range = entry.getKey();
          json.name("symbol")
            .beginObject()
            .prop("startLine", range.start().line())
            .prop("endLine", range.end().line())
            .prop("startOffset", range.start().lineOffset())
            .prop("endOffset", range.end().lineOffset())
            .endObject();
        }

        json.name("locations").beginArray();
        for (TextRange range : entry.getValue()) {
          json.beginObject()
            .prop("startLine", range.start().line())
            .prop("endLine", range.end().line())
            .prop("startOffset", range.start().lineOffset())
            .prop("endOffset", range.end().lineOffset())
            .endObject();
        }
        json.endArray();
      }
      json.endObject();
    }
    json.endArray();
  }

  private void writeErrors(JsonWriter json) {
    json.name("errors");
    json.beginArray();
    analyzerResult.errors().forEach(error -> {
      String message = error.message();
      json.beginObject().prop("message", message);
      TextPointer location = error.location();
      if (location != null) {
        json
          .prop("line", location.line())
          .prop("lineOffset", location.lineOffset());
      } else if (message != null) {
        Matcher matcher = parseErrorPattern.matcher(message);
        if (matcher.matches()) {
          json
            .prop("line", Integer.parseInt(matcher.group(1)))
            .prop("lineOffset", Integer.parseInt(matcher.group(2)) - 1);
        }
      }
      json.endObject();
    });
    json.endArray();
  }

  // success == errors.isEmpty
  @Deprecated
  private void writeSuccess(JsonWriter json) {
    json.prop("success", analyzerResult.success());
  }
}
