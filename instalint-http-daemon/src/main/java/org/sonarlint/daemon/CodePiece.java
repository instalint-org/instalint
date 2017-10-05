package org.sonarlint.daemon;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class CodePiece {
  private final PieceType type;
  private String text;

  public CodePiece(PieceType type) {
    this.type = type;
  }

  public PieceType getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public CodePiece setText(String text) {
    this.text = text;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CodePiece codePiece = (CodePiece) o;

    if (type != codePiece.type)
      return false;
    return text != null ? text.equals(codePiece.text) : codePiece.text == null;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (text != null ? text.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CodePiece{" +
      type +
      (text == null ? "" : ", text='" + text + '\'') +
      '}';
  }

  public List<CodePiece> splitAt(int i) {
    if (i == text.length()) {
      return Collections.singletonList(this);
    }
    return asList(
      new CodePiece(PieceType.TEXT).setText(text.substring(0, i)),
      new CodePiece(PieceType.TEXT).setText(text.substring(i)));
  }
}
