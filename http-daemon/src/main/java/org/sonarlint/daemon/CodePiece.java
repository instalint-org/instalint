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

import java.util.Collection;
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

  public CodePiece setText(String text) {
    this.text = text;
    return this;
  }

  public String getText() {
    return text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CodePiece codePiece = (CodePiece) o;

    if (type != codePiece.type) return false;
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
            new CodePiece(PieceType.TEXT).setText(text.substring(0,i)),
            new CodePiece(PieceType.TEXT).setText(text.substring(i))
    );
  }
}
