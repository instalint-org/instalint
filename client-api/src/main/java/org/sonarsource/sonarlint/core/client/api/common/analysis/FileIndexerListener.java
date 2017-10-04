package org.sonarsource.sonarlint.core.client.api.common.analysis;

import java.io.File;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.TextPointer;

public interface FileIndexerListener {
  void indexed(File file);

  int count();
}
