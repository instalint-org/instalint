package io.instalint.cli;

import org.junit.BeforeClass;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

public class JavaScriptExecutionTest extends AnalyzerExecutionTest {
  @BeforeClass
  public static void beforeClass() {
    analyzerFilename = "sonar-javascript-plugin-3.1.1.5128.jar";

    analyzerFilesExtension = "js";

    expected = expected()
      .fileCount(2)
      .failedFileCount(1)
      .issueCount(1)
      .highlight(TypeOfText.KEYWORD, range(1, 0, 1, 3))
      .highlight(TypeOfText.KEYWORD, range(2, 0, 2, 3))
      .highlight(TypeOfText.KEYWORD, range(2, 7, 2, 9))
      .highlight(TypeOfText.CONSTANT, range(1, 14, 1, 15))
      .highlight(TypeOfText.CONSTANT, range(1, 11, 1, 12))
      .highlight(TypeOfText.CONSTANT, range(1, 17, 1, 18))
      .symbolRef(range(2, 15, 2, 16),
        ranges(range(4, 0, 4, 1)))
      .symbolRef(range(1, 4, 1, 7),
        ranges(range(2, 10, 2, 13)))
      .symbolRef(range(2, 5, 2, 6),
        ranges(range(3, 16, 3, 17)))
      .symbolRef(range(3, 4, 3, 11),
        ranges())
    ;

    result = analyzerHelper();
  }

}
