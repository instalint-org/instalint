package io.instalint.core;

import java.util.List;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

public class AnalyzerResult {
  private List<Issue> issues;

  public List<Issue> issues() {
    return issues;
  }
}
