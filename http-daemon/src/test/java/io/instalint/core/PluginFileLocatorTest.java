package io.instalint.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginFileLocatorTest {
  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test(expected = IOException.class)
  public void should_throw_if_basedir_nonexistent() throws IOException {
    PluginFileLocator.findFirst(tmp.newFolder().toPath().resolve("nonexistent"), "nonexistent");
  }

  @Test(expected = FileNotFoundException.class)
  public void should_throw_if_found_nothing() throws IOException {
    PluginFileLocator.findFirst(tmp.newFolder().toPath(), "nonexistent");
  }

  @Test
  public void should_find_matching() throws IOException {
    Path basedir = tmp.newFolder().toPath();
    Files.createFile(basedir.resolve("file1.aaa"));
    Files.createFile(basedir.resolve("file1.bbb"));
    assertThat(PluginFileLocator.findFirst(basedir, "**.bbb").getFileName().toString()).isEqualTo("file1.bbb");
  }
}
