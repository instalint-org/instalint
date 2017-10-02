package io.instalint.core;

import org.sonarlint.daemon.LanguagePlugin;

public interface AnalyzerExecutor {
  AnalyzerResult execute(LanguagePlugin languagePlugin, String code);
}
