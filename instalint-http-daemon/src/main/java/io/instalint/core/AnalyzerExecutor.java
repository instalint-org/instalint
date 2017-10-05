package io.instalint.core;

import io.instalint.daemon.LanguagePlugin;

public interface AnalyzerExecutor {
  AnalyzerResult execute(LanguagePlugin languagePlugin, String code);
}
