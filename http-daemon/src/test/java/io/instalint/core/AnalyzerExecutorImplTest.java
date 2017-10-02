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
    String code = "    var arr = [1, 2, 3];\n" +
      "    for (i in arr) {\n" +
      "        console.log(i);\n" +
      "    }";

    AnalyzerResult result = execute(code);

    assertThat(result.issues()).hasSize(1);
  }

  private AnalyzerResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
}
