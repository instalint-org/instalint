
// test cases: https://jsfiddle.net/p9vdzhtd/14/
function lineOffsets(code) {
	var offsets = [];
    var pos = 0;
    code.split("\n").forEach(line => {
        offsets.push(pos);
        pos += line.length + 1;
    });
    offsets.push(pos);

    return offsets;
}

function doFormat(response) {
    var code = response.code;

    var offsets = lineOffsets(code);

    var lineHighlightStart = [];
    var lineHighlightEnd = [];
    response.highlightings.forEach(highlight => {
        var start = offsets[highlight.startLine - 1] + highlight.startOffset;
        var end = offsets[highlight.endLine - 1] + highlight.endOffset;
        lineHighlightStart[start] = highlight.cssClass;
        lineHighlightEnd[end] = 1;
    });

    var issueStart = [];
    var issueEnd = [];
    response.issues.map(issue => issue.textRange).forEach(highlight => {
        var start = offsets[highlight.startLine - 1] + highlight.startOffset;
        var end = offsets[highlight.endLine - 1] + highlight.endOffset;
        issueStart[start] = 1;
        issueEnd[end] = 1;
    });

    var characterIndex;
    var result = "";
    for (characterIndex = 0; characterIndex <= code.length; characterIndex++) {
        if (lineHighlightEnd[characterIndex]) {
            result += '</span>';
        }
        if (issueEnd[characterIndex]) {
            result += '</span>';
        }
        if (issueStart[characterIndex]) {
            result += '<span class="source-line-code-issue">';
        }
        if (lineHighlightStart[characterIndex]) {
            result += '<span class="' + lineHighlightStart[characterIndex] + '">';
        }
        if (characterIndex < code.length) {
            result += code[characterIndex];
        }
    }

    return result;
}
