/*
 * SonarLint Daemon
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

import org.sonarsource.sonarlint.daemon.proto.SonarlintDaemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Underliner {

  private final List<CodePiece> pieces = new ArrayList<>();

  public Underliner(String content) {
    Arrays.stream(content.split("\n")).forEach(l -> {
      pieces.add(new CodePiece(PieceType.LINE_START));
      pieces.add(new CodePiece(PieceType.TEXT).setText(l));
      pieces.add(new CodePiece(PieceType.LINE_END));
    });
  }

  public List<CodePiece> underline(List<SonarlintDaemon.Issue> issues) {
    issues.forEach(issue -> {
      int pieceIndex = 0;
      for (int linesToSkip = issue.getStartLine(); linesToSkip > 0; pieceIndex++) {
        if (pieces.get(pieceIndex).getType() == PieceType.LINE_START) {
          linesToSkip--;
        }
      }
      int startLineOffset = issue.getStartLineOffset();
      int carretPosition = 1;
      for (; carretPosition < startLineOffset; ) {
        while (pieces.get(pieceIndex).getType() != PieceType.TEXT) {
          pieceIndex++;
        }
        if (startLineOffset < carretPosition + pieces.get(pieceIndex).getText().length()) {
          pieces.addAll(pieceIndex, pieces.remove(pieceIndex).splitAt(startLineOffset - carretPosition));
        } else {
          break;
        }
        carretPosition += pieces.get(pieceIndex).getText().length();
        pieceIndex++;
      }
      pieces.add(pieceIndex, new CodePiece(PieceType.UNDERLINE_START));
      pieceIndex++;

      int endLineOffset = issue.getEndLineOffset();
      for (; carretPosition <= endLineOffset; ) {
        while (pieces.get(pieceIndex).getType() != PieceType.TEXT) {
          pieceIndex++;
        }
        if (endLineOffset <= carretPosition + pieces.get(pieceIndex).getText().length()) {
          pieces.addAll(pieceIndex, pieces.remove(pieceIndex).splitAt(endLineOffset+1 - carretPosition));
        } else {
          pieceIndex++;
          break;
        }
        carretPosition += pieces.get(pieceIndex).getText().length();
        pieceIndex++;
      }
      pieces.add(pieceIndex, new CodePiece(PieceType.UNDERLINE_END));
    });
    return pieces;
  }
}
