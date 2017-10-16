package io.instalint.core;

import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LanguagePlugin {

  private final URL url;
  private final String languageVersion;
  private final String inputFileExtension;

  LanguagePlugin(URL url, String languageVersion, String inputFileExtension) {
    this.url = url;
    this.languageVersion = languageVersion;
    this.inputFileExtension = inputFileExtension;
  }

  URL getUrl() {
    return url;
  }

  String getLanguageVersion() {
    return languageVersion;
  }

  String getInputFileExtension() {
    return inputFileExtension;
  }
}
