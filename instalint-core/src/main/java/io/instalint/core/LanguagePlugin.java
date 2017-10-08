package io.instalint.core;

import java.net.URL;

public class LanguagePlugin {
  private final URL url;
  private final String languageVersion;
  private final String inputFileExtension;

  public LanguagePlugin(URL url, String languageVersion, String inputFileExtension) {
    this.url = url;
    this.languageVersion = languageVersion;
    this.inputFileExtension = inputFileExtension;
  }

  public URL getUrl() {
    return url;
  }

  public String getLanguageVersion() {
    return languageVersion;
  }

  public String getInputFileExtension() {
    return inputFileExtension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    LanguagePlugin that = (LanguagePlugin) o;

    return url != null ? url.equals(that.url) : that.url == null;
  }

  @Override
  public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }
}
