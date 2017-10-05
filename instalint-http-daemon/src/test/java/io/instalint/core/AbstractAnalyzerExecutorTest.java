package io.instalint.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import io.instalint.daemon.LanguagePlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public abstract class AbstractAnalyzerExecutorTest {

  private static AnalyzerExecutor executor = new AnalyzerExecutorImpl();

  private LanguagePlugin languagePlugin = newLanguagePlugin();

  private LanguagePlugin newLanguagePlugin() {
    try {
      String languageCode = languageCode();
      String inputFileExtension = InputFileExtensions.fromLanguageCode(languageCode);
      return new LanguagePlugin(findPluginFile(languageCode).toUri().toURL(), null, inputFileExtension);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    // unreachable
    return null;
  }

  private Path findPluginFile(String languageCode) throws IOException {
    String glob = String.format("**/sonar-%s-plugin-*.jar", languageCode);
    return PluginFileLocator.findFirst(Paths.get("../sonarlint-core/target/plugins"), glob);
  }

  abstract String languageCode();

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
