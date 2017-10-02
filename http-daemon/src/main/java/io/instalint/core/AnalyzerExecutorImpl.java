package io.instalint.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonarlint.daemon.LanguagePlugin;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.common.analysis.HighlightingListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.SymbolRefsListener;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

public class AnalyzerExecutorImpl implements AnalyzerExecutor {
  @Override
  public AnalyzerResult execute(LanguagePlugin languagePlugin, String code) {
    StandaloneGlobalConfiguration globalConfig = StandaloneGlobalConfiguration.builder()
      .addPlugin(languagePlugin.getUrl())
      .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    Path tmp;
    try {
      tmp = newTempDir();
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Could not create temp dir");
    }

    Path workDir;
    try {
      workDir = newDir(tmp.resolve("work"));
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Could not create workdir");
    }

    Path path = tmp.resolve("code.js");

    ClientInputFile clientInputFile = new ClientInputFile() {
      @Override
      public String getPath() {
        return path.toString();
      }

      @Override
      public boolean isTest() {
        return false;
      }

      @Override
      public Charset getCharset() {
        return StandardCharsets.UTF_8;
      }

      @Override
      public InputStream inputStream() throws IOException {
        return new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public String contents() throws IOException {
        return code;
      }
    };

    Iterable<ClientInputFile> inputFiles = Collections.singleton(clientInputFile);

    Map<String, String> extraProperties = new HashMap<>();
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(workDir, inputFiles, extraProperties);

    List<Issue> issues = new ArrayList<>();
    IssueListener issueListener = issues::add;

    LogOutput logOutput = (formattedMessage, level) -> { };

    HighlightingListener highlightingListener =
      highlighting -> highlighting.forEach(hl -> {});

    SymbolRefsListener symbolRefsListener =
      referencesBySymbol -> {};

    AnalysisResults results = engine.analyze(
      config,
      issueListener,
      highlightingListener,
      symbolRefsListener,
      logOutput);

    return new AnalyzerResult() {
      @Override
      public List<Issue> issues() {
        return issues;
      }
    };
  }

  private static Path newDir(Path path) throws IOException {
    return Files.createDirectories(path);
  }

  private static Path newTempDir() throws IOException {
    return Files.createTempDirectory("sonarlint-");
  }
}
