/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.daemon;

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
      try {
        Files.createDirectories(storeDir);
      } catch (IOException e) {
        e.printStackTrace();
        throw new IllegalStateException("Cannot create store");
      }
      LOGGER.info(() -> "storeDir: " + storeDir.toAbsolutePath());
    }
  }

  public String load(String storedAs) {
    RandomString.validate(storedAs);
    try {
      return new String(Files.readAllBytes(getFile(storedAs)), CHARSET);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Cannot read file");
    }
  }

  public String store(String code) {
    String storedAs = RandomString.generate();
    try {
      Files.write(getFile(storedAs), code.getBytes(CHARSET), StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Cannot write file");
    }
    return storedAs;
  }

  private Path getFile(String storedAs) {

    // avoid having too many files in one directory
    Path dir = storeDir.resolve(storedAs.substring(0, 2));

    try {
      Files.createDirectories(dir);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Cannot create store subdir");
    }
    return dir.resolve(storedAs);
  }
}
