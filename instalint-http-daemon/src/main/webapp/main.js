
const doneTypingInterval = 1000;  //time in ms
let languageVersion = "latest";
let storedAs = "";

function onInit() {
    useLocationHash();
    updateLocationHash();
    window.onhashchange = onLocationHashChange;
    analyze(false);
}

function onLocationHashChange() {
    useLocationHash();
    updateLocationHash();
}

function onLanguageChange() {
    languageVersion = "latest";
    updateLocationHash();
    analyze(false);
}

var lastProcessedInput = new Date().getTime();
var timer;

function onInput() {
    var now = new Date().getTime();
    var executionDelay = lastProcessedInput + doneTypingInterval - now;
    lastProcessedInput = new Date().getTime();
    if (executionDelay < 0) {
        onInitAndNotTooOften();
    } else {
        if (timer) {
            clearTimeout(timer);
        }
        timer = setTimeout(onInitAndNotTooOften, executionDelay);
    }
}

function onInitAndNotTooOften() {
    if (timer) {
        clearTimeout(timer);
    }
    lastProcessedInput = new Date().getTime();
    storedAs = "";
    analyze(false);
}

function onStoreRequest() {
    analyze(true);
}

function useLocationHash() {
    var path = document.location.hash;
    var pathPattern = new RegExp("^#(/.*)+$", "");

    var pathMatches = pathPattern.exec(path);
    var newLanguageValue = "JavaScript";
    var newLanguageVersion = "latest";
    var newStoredAs = "";
    var languageSelect = document.getElementById("language");
    if (pathMatches) {
        var pathPartPattern = new RegExp("^(/(.*?))(/.*$|$)", "");

        var languageMatches = pathPartPattern.exec(pathMatches[1]);
        if (languageMatches) {
            var language = languageMatches[2];

            for (var optionCounter in languageSelect.options) {
                var option = languageSelect.options[optionCounter];
                if (option.label == language || option.value == language) {
                    newLanguageValue = option.value;
                }
            }

            var languageVersionMatches = pathPartPattern.exec(languageMatches[3]);
            if (languageVersionMatches) {
                newLanguageVersion = languageVersionMatches[2];

                var storedAsMatches = pathPartPattern.exec(languageVersionMatches[3]);
                if (storedAsMatches) {
                    newStoredAs = storedAsMatches[2];
                }
            }
        }
    }

    if (languageSelect.value != newLanguageValue || languageVersion != newLanguageVersion || storedAs != newStoredAs) {
        if (languageSelect.value != newLanguageValue) {
            languageSelect.value = newLanguageValue;
        }
        if (languageVersion != newLanguageVersion) {
            languageVersion = newLanguageVersion;
        }
        if (storedAs != newStoredAs) {
            storedAs = newStoredAs;
        }
        updateLocationHash();
        analyze(false);
    }
}

function updateLocationHash() {
    var languageSelect = document.getElementById("language");
    var language = languageSelect.value;

    var newPath = "#/" + language;
    if (languageVersion != "latest") {
        newPath += "/" + languageVersion;
        if (storedAs != "") {
            newPath += "/" + storedAs;
        }
    }

    if (document.location.hash != newPath) {
        document.location.hash = newPath;
    }
}

function analyze(store) {
    console.log("analyze");
    document.getElementById("output").style.opacity = 0.5;
    var loc = window.location;
    var baseUrl = loc.protocol + "//" + loc.hostname + (loc.port? ":"+loc.port : "") + "/" + loc.pathname.split('/')[1];
    var url = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "analyze";
    var method = "POST";
    var async = true;
    var request = new XMLHttpRequest();
    request.onload = function () {

        var response = JSON.parse(request.responseText);

        document.getElementById("output").innerHTML = doFormat(response);
        document.getElementById("output").style.opacity = 1;

        if (storedAs != "" || store) {
            document.getElementById("input").value = response.code;
            if (response.storedAs) {
                storedAs = response.storedAs;
                languageVersion = response.languageVersion;
            } else {
                storedAs = "";
            }
        }
        updateLocationHash();
    }

    var language = document.getElementById("language").value;
    var code = document.getElementById("input").value;

    if (code === "" && storedAs === "") {
        document.getElementById("output").innerHTML = newContent;
    } else {
        request.open(method, url, async);
        request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var params = "language=" + encodeURIComponent(language) + "&languageVersion=" + encodeURIComponent(languageVersion);
        if (storedAs != "") {
            params += "&restore=" + storedAs;
        } else {
            if (store == true) {
                params += "&store=true";
            }
            params += "&code=" + encodeURIComponent(code);
        }
        request.send(params);
        document.getElementById("output").style.opacity = 0.7;
    }
}

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
