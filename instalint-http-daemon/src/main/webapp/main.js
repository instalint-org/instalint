
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

function onInput() {
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
    var languageSelectLabel = "JavaScript";
    for (var optionCounter in languageSelect.options) {
        var option = languageSelect.options[optionCounter];
        if (option.value == language) {
            languageSelectLabel = option.label;
        }
    }

    var newPath = "#/" + lgitanguage;
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
    document.getElementById("result").style.opacity = 0.5;
    var loc = window.location;
    var baseUrl = loc.protocol + "//" + loc.hostname + (loc.port? ":"+loc.port : "") + "/" + loc.pathname.split('/')[1];
    var url = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "analyze";
    var method = "POST";
    var async = true;
    var request = new XMLHttpRequest();
    request.onload = function () {

        var response = JSON.parse(request.responseText);

        document.getElementById("result").innerHTML = "";
        if (!response.success) {
            document.getElementById("result").innerHTML +=
            '<div class="issue">Parsing failed. Please make sure that your code does not contain any syntax errors.</div>';
        }
        for (let issueCounter in response.errors) {
            let issue = response.errors[issueCounter];
            document.getElementById("result").innerHTML +=
            '<div class="issue">'
            + issue.message
            + '<div class="issueDotDotDot">...</div>'
            + '</div>';
        }
        for (let issueCounter in response.issues) {
            let issue = response.issues[issueCounter];
            document.getElementById("result").innerHTML +=
            '<div class="issue">'
            + issue.message
            + '<div class="issueDotDotDot">...</div>'
            + '</div>';
        }
        document.getElementById("result").style.opacity = 1;

        var newContent = "";
        var firstLine = true;
        for (var line in response.lines) {
            if (firstLine == true) {
                firstLine = false;
            } else {
                newContent += "<br/>";
            }
            var l = response.lines[line].code;
            newContent += l;
        }

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
        document.getElementById("output").innerHTML = newContent;
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
        document.getElementById("result").style.opacity = 0.7;
    }
}
