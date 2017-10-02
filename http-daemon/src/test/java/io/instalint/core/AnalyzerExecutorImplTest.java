package io.instalint.core;

import java.io.File;
import java.net.MalformedURLException;
import org.junit.Test;
import org.sonarlint.daemon.LanguagePlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AnalyzerExecutorImplTest {

  static AnalyzerExecutor executor = new AnalyzerExecutorImpl();

  static LanguagePlugin languagePlugin = newLanguagePlugin();

  private static final String validExampleCode =
    "    var arr = [1, 2, 3];\n" +
    "    for (i in arr) {\n" +
    "        console.log(i);\n" +
    "    }";

  private static LanguagePlugin newLanguagePlugin() {
    try {
      return new LanguagePlugin(new File("../core/target/plugins/sonar-javascript-plugin-3.1.1.5128.jar").toURI().toURL(), null);
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
  }

  @Test
  public void should_report_highlightings() {
    AnalyzerResult result = execute(validExampleCode);
    assertThat(result.highlightings()).hasSize(6);
  }

  @Test
  public void should_report_symbol_refs() {
    AnalyzerResult result = execute(validExampleCode);
    assertThat(result.symbolRefs()).hasSize(4);
  }

  private AnalyzerResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
}
