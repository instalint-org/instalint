package io.instalint.daemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

public class Underliner {

  private final List<CodePiece> pieces = new ArrayList<>();

  public Underliner(String content) {
    Arrays.stream(content.split("\n")).forEach(l -> {
      pieces.add(new CodePiece(PieceType.LINE_START));
      pieces.add(new CodePiece(PieceType.TEXT).setText(l));
      pieces.add(new CodePiece(PieceType.LINE_END));
    });
  }

  public List<CodePiece> underline(List<Issue> issues) {
    issues.forEach(issue -> {
      int pieceIndex = 0;
      for (int linesToSkip = issue.getStartLine(); linesToSkip > 0; pieceIndex++) {
        if (pieces.get(pieceIndex).getType() == PieceType.LINE_START) {
          linesToSkip--;
        }
      }
      int startLineOffset = issue.getStartLineOffset();
      int carretPosition = 1;
      for (; carretPosition < startLineOffset;) {
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
      for (; carretPosition <= endLineOffset;) {
        while (pieces.get(pieceIndex).getType() != PieceType.TEXT) {
          pieceIndex++;
        }
        if (endLineOffset <= carretPosition + pieces.get(pieceIndex).getText().length()) {
          pieces.addAll(pieceIndex, pieces.remove(pieceIndex).splitAt(endLineOffset + 1 - carretPosition));
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
