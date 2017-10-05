package io.instalint.cli;

import org.junit.BeforeClass;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

public class PhpExecutionTest extends AnalyzerExecutionTest {
  @BeforeClass
  public static void beforeClass() {
    analyzerFilename = "sonar-php-plugin-2.10.0.2087.jar";

    analyzerFilesExtension = "php";

    expected = expected()
      .fileCount(3)
      .failedFileCount(1)
      .issueCount(8)
      .highlight(TypeOfText.COMMENT, range(2, 0, 2, 21))
      .symbolRef(range(17, 2, 17, 4),
        ranges())
      .symbolRef(range(16, 9, 16, 10),
        ranges());

    result = analyzerHelper();
  }

}
