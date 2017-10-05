package io.instalint.daemon;

import java.util.Arrays;
import java.util.List;
import javax.annotation.CheckForNull;
import org.junit.Assert;
import org.junit.Test;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class UnderlinerTest {

  @Test
  public void should_return_single_line() {
    assertPieces(
      "bla",
      emptyList(),
      new CodePiece(PieceType.LINE_START),
      new CodePiece(PieceType.TEXT).setText("bla"),
      new CodePiece(PieceType.LINE_END));
  }

  @Test
  public void should_underline_line() {
    assertPieces(
      "bla",
      singletonList(issueBuilder().setStartLine(1).setStartLineOffset(1).setEndLineOffset(3).setEndLine(1).build()),
      new CodePiece(PieceType.LINE_START),
      new CodePiece(PieceType.UNDERLINE_START),
      new CodePiece(PieceType.TEXT).setText("bla"),
      new CodePiece(PieceType.UNDERLINE_END),
      new CodePiece(PieceType.LINE_END));
  }

  @Test
  public void should_underline_carret() {
    assertPieces(
      "bla",
      singletonList(issueBuilder().setStartLine(1).setStartLineOffset(2).setEndLineOffset(2).setEndLine(1).build()),
      new CodePiece(PieceType.LINE_START),
      new CodePiece(PieceType.TEXT).setText("b"),
      new CodePiece(PieceType.UNDERLINE_START),
      new CodePiece(PieceType.TEXT).setText("l"),
      new CodePiece(PieceType.UNDERLINE_END),
      new CodePiece(PieceType.TEXT).setText("a"),
      new CodePiece(PieceType.LINE_END));
  }

  private void assertPieces(String content, List<Issue> issues, CodePiece... expected) {
    Assert.assertEquals(Arrays.asList(expected), new Underliner(content).underline(issues));
  }

  IssueBuilder issueBuilder() {
    return new IssueBuilder();
  }

  static class IssueBuilder {

    private int startLine;
    private int startLineOffset;
    private int endLine;
    private int endLineOffset;

    public IssueBuilder setStartLine(int startLine) {
      this.startLine = startLine;
      return this;
    }

    public IssueBuilder setStartLineOffset(int startLineOffset) {
      this.startLineOffset = startLineOffset;
      return this;
    }

    public IssueBuilder setEndLine(int endLine) {
      this.endLine = endLine;
      return this;
    }

    public IssueBuilder setEndLineOffset(int endLineOffset) {
      this.endLineOffset = endLineOffset;
      return this;
    }

    Issue build() {
      return new Issue() {
        @Override
        public String getSeverity() {
          return null;
        }

        @CheckForNull
        @Override
        public String getType() {
          return null;
        }

        @Override
        public String getRuleKey() {
          return null;
        }

        @Override
        public String getRuleName() {
          return null;
        }

        @Override
        public List<Flow> flows() {
          return null;
        }

        @CheckForNull
        @Override
        public ClientInputFile getInputFile() {
          return null;
        }

        @CheckForNull
        @Override
        public Integer getStartLine() {
          return startLine;
        }

        @CheckForNull
        @Override
        public Integer getEndLine() {
          return endLine;
        }

        @CheckForNull
        @Override
        public Integer getStartLineOffset() {
          return startLineOffset;
        }

        @CheckForNull
        @Override
        public Integer getEndLineOffset() {
          return endLineOffset;
        }

        @CheckForNull
        @Override
        public String getMessage() {
          return null;
        }
      };
    }
  }
}
