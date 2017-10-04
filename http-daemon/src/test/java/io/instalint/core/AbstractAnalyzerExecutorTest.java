package io.instalint.core;

import java.io.File;
import java.net.MalformedURLException;
import org.junit.Test;
import org.sonarlint.daemon.LanguagePlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public abstract class AbstractAnalyzerExecutorTest {

  static AnalyzerExecutor executor = new AnalyzerExecutorImpl();

  private LanguagePlugin languagePlugin = newLanguagePlugin();

  private LanguagePlugin newLanguagePlugin() {
    try {
      return new LanguagePlugin(new File("../core/target/plugins", filename()).toURI().toURL(), null, inputFileExtension());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    }
    // unreachable
    return null;
  }

  abstract String filename();

  abstract String inputFileExtension();

  abstract String validExampleCode();

  abstract String invalidExampleCode();

  abstract int issueCount();

  abstract int highlightingCount();

  abstract int symbolRefCount();

  @Test
  public void should_report_issues() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.issues()).hasSize(issueCount());
  }

  @Test
  public void should_report_highlightings() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.highlightings()).hasSize(highlightingCount());
  }

  @Test
  public void should_report_symbol_refs() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.symbolRefs()).hasSize(symbolRefCount());
  }

  @Test
  public void should_report_analysis_failed() {
    AnalyzerResult result = execute(invalidExampleCode() + validExampleCode());
    assertThat(result.success()).isFalse();
    assertThat(result.issues()).isEmpty();
    assertThat(result.highlightings()).isEmpty();
    assertThat(result.symbolRefs()).isEmpty();
  }

  private AnalyzerResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
}
