package io.instalint.daemon;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class SourceRepository {

  public static final Charset CHARSET = StandardCharsets.UTF_8;
  public static final Logger LOGGER = Logger.getLogger(SourceRepository.class.getName());

  private Path storeDir;

  public void init(Path workDir) {
    if (storeDir == null) {
      storeDir = workDir.resolve("store");
      LOGGER.info(() -> "storeDir: " + storeDir.toAbsolutePath());
      try {
        Files.createDirectories(storeDir);
      } catch (IOException e) {
        LOGGER.warning("Could not create store");
      }
    }
  }

  public String load(String storedAs) {
    RandomString.validate(storedAs);
    try {
      return new String(Files.readAllBytes(getFile(storedAs)), CHARSET);
    } catch (IOException e) {
      LOGGER.warning("Could not read code from store: " + storedAs);
    }
    return "";
  }

  public String store(String code) {
    String storedAs = RandomString.generate();
    try {
      Files.write(getFile(storedAs), code.getBytes(CHARSET), StandardOpenOption.CREATE);
    } catch (IOException e) {
      LOGGER.warning("Could not write code to store: " + storedAs);
    }
    return storedAs;
  }

  private Path getFile(String storedAs) throws IOException {
    // avoid having too many files in one directory
    Path dir = storeDir.resolve(storedAs.substring(0, 2));
    Files.createDirectories(dir);
    return dir.resolve(storedAs);
  }
}
