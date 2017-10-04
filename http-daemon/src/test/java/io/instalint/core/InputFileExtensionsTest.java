package io.instalint.core;

import org.junit.Test;

import static io.instalint.core.InputFileExtensions.fromLanguageCode;
import static org.assertj.core.api.Assertions.assertThat;

public class InputFileExtensionsTest {
  @Test
  public void should_return_js_for_javascript() {
    assertThat(fromLanguageCode("javascript")).isEqualTo("js");
  }

  @Test
  public void should_return_self_for_nonexistent() {
    assertThat(fromLanguageCode("foo")).isEqualTo("foo");
    assertThat(fromLanguageCode("bar")).isEqualTo("bar");
    assertThat(fromLanguageCode("baz")).isEqualTo("baz");
  }
}
