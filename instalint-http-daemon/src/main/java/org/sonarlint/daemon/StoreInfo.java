package org.sonarlint.daemon;

public class StoreInfo {

  private String languageVersion;
  private String storedAs;

  public StoreInfo(String languageVersion, String storedAs) {
    this.languageVersion = languageVersion;
    this.storedAs = storedAs;
  }

  public String getLanguageVersion() {
    return languageVersion;
  }

  public StoreInfo setLanguageVersion(String languageVersion) {
    this.languageVersion = languageVersion;
    return this;
  }

  public String getStoredAs() {
    return storedAs;
  }

  public StoreInfo setStoredAs(String storedAs) {
    this.storedAs = storedAs;
    return this;
  }
}
