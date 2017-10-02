package io.instalint.core;

import java.util.List;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

public interface AnalyzerResult {

  List<Issue> issues();
}
