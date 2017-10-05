package org.sonarsource.sonarlint.core.client.api.common.analysis;

import java.io.File;

public interface FileIndexerListener {
  void indexed(File file);

  int count();
}
