package io.instalint.core;

import java.util.HashMap;
import java.util.Map;

public class InputFileExtensions {

  static Map<String, String> mappings = new HashMap<>();
  static {
    mappings.put("javascript", "js");
  }

  private InputFileExtensions() {
    // utility class, forbidden constructor
  }

  public static String fromLanguageCode(String languageCode) {
    return mappings.getOrDefault(languageCode, languageCode);
  }
}
