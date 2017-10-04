package io.instalint.core;

public class JavaScriptAnalyzerExecutorTest extends AbstractAnalyzerExecutorTest {

  @Override
  String filename() {
    return "sonar-javascript-plugin-3.1.1.5128.jar";
  }

  @Override
  String inputFileExtension() {
    return "js";
  }

  @Override
  String validExampleCode() {
    return "    var arr = [1, 2, 3];\n" +
      "    for (i in arr) {\n" +
      "        console.log(i);\n" +
      "    }";
  }

  @Override
  String invalidExampleCode() {
    return "function hello(";
  }

  @Override
  int issueCount() {
    return 1;
  }

  @Override
  int highlightingCount() {
    return 6;
  }

  @Override
  int symbolRefCount() {
    return 4;
  }
}
