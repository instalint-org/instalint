package io.instalint.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

class PluginFileLocator {

  private PluginFileLocator() {
    // utility class, forbidden constructor
  }

  static Path findFirst(Path basedir, String glob) throws IOException {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
    try (Stream<Path> stream = Files.list(basedir)) {
      return stream
        .filter(matcher::matches)
        .findFirst()
        .orElseThrow(() -> new FileNotFoundException(basedir + "/.../" + glob));
    }
  }
}
