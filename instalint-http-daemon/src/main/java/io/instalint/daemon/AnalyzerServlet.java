package io.instalint.daemon;

import io.instalint.core.Analyzer;
import io.instalint.core.AnalyzerResult;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnalyzerServlet extends HttpServlet {

  private static Backend backend;

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
    Analyzer analyzer = backend.retrieve(languageParam, languageVersionParam);

    if ("true".equals(req.getParameter("store"))) {
      storedAs = backend.store(code);
    }

    AnalyzerResult result = analyzer.apply(code);

    new ResponseMessage(analyzer.getLanguageVersion(), storedAs, code, result).writeTo(resp);
  }

  private String getLanguageVersion(HttpServletRequest req) {
    String languageVersion = req.getParameter("languageVersion");
    if (languageVersion == null || languageVersion.isEmpty()) {
      languageVersion = "latest";
    } else if (!languageVersion.matches("^[a-zA-Z0-9\\.]*$")) {
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
