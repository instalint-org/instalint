package io.instalint.cli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  JavaScriptExecutionTest.class,
  PhpExecutionTest.class,
  PythonExecutionTest.class
})
public class AnalyzerExecutionTestSuite {
}
