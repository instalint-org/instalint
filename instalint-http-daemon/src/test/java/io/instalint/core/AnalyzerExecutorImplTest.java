package io.instalint.core;

import java.io.File;
import java.net.MalformedURLException;
import org.junit.Test;
import org.sonarlint.daemon.LanguagePlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AnalyzerExecutorImplTest {

  private static final String validExampleCode = "    var arr = [1, 2, 3];\n" +
    "    for (i in arr) {\n" +
    "        console.log(i);\n" +
    "    }";
  private static final String invalidExampleCode = "function hello(";
  static AnalyzerExecutor executor = new AnalyzerExecutorImpl();
  static LanguagePlugin languagePlugin = newLanguagePlugin();

  private static LanguagePlugin newLanguagePlugin() {
    try {
      return new LanguagePlugin(new File("../sonarlint-core/target/plugins/sonar-javascript-plugin-3.1.1.5128.jar").toURI().toURL(), null);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    }
    // unreachable
    return null;
  }

  @Test
  public void should_report_issues() {
    AnalyzerResult result = execute(validExampleCode);
    assertThat(result.issues()).hasSize(1);
    assertThat(result.success()).isTrue();
  }

  @Test
  public void should_report_highlightings() {
    AnalyzerResult result = execute(validExampleCode);
    assertThat(result.highlightings()).hasSize(6);
    assertThat(result.success()).isTrue();
  }

  @Test
  public void should_report_symbol_refs() {
    AnalyzerResult result = execute(validExampleCode);
    assertThat(result.symbolRefs()).hasSize(4);
    assertThat(result.success()).isTrue();
  }

  @Test
  public void should_report_analysis_failed() {
    AnalyzerResult result = execute(invalidExampleCode + validExampleCode);
    assertThat(result.success()).isFalse();
    assertThat(result.issues()).isEmpty();
    assertThat(result.highlightings()).isEmpty();
    assertThat(result.symbolRefs()).isEmpty();
  }

  private AnalyzerResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
}
