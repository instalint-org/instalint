package io.instalint.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class PluginLocator {
  public static Path findFirst(Path basedir, String glob) throws IOException {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
    return Files.list(basedir)
      .filter(matcher::matches)
      .findFirst()
      .orElseThrow(() -> new FileNotFoundException(basedir + "/.../" + glob));
  }
}
