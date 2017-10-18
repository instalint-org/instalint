
const doneTypingInterval = 1000;  //time in ms
let languageVersion = "latest";
let storedAs = "";

let onTheFlyAnalysisTimeout;
let lastCodeLength = 0;
const minLengthDifferenceToSkipTimeout = 5;

let languageExampleFunctions = {
    "JavaScript": addJavaScriptExamples,
    "Java": addJavaExamples,
    "Python": addPythonExamples,
    "PHP": addPhpExamples,
};

function onInit() {
    useLocationHash();
    updateLocationHash();
    window.onhashchange = onLocationHashChange;
    setNewLanguage(getCurrentLanguage());

    lastCodeLength = getCode().length;
    analyze(false);
}

function onLocationHashChange() {
    useLocationHash();
    updateLocationHash();
}

function onLanguageChange() {
    languageVersion = "latest";
    analyzeUserInput();
}

function onInput() {
    clearTimeout(onTheFlyAnalysisTimeout);

    var code = getCode();
    if (Math.abs(code.length - lastCodeLength) > minLengthDifferenceToSkipTimeout) {
        analyzeUserInput();
    } else {
        onTheFlyAnalysisTimeout = setTimeout(() => analyzeUserInput(), doneTypingInterval);
    }
    lastCodeLength = code.length;
}

function analyzeUserInput() {
    storedAs = "";
    updateLocationHash();
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
    if (pathMatches) {
        var pathPartPattern = new RegExp("^(/(.*?))(/.*$|$)", "");

        var languageMatches = pathPartPattern.exec(pathMatches[1]);
        if (languageMatches) {
            newLanguageValue = languageMatches[2];

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

    const currentLanguage = getCurrentLanguage();

    if (currentLanguage !== newLanguageValue || languageVersion !== newLanguageVersion || storedAs !== newStoredAs) {
        if (currentLanguage !== newLanguageValue) {
            setNewLanguage(newLanguageValue);
        }
        if (languageVersion !== newLanguageVersion) {
            languageVersion = newLanguageVersion;
        }
        if (storedAs !== newStoredAs) {
            storedAs = newStoredAs;
        }
        updateLocationHash();
        analyze(false);
    }

}

function getCurrentLanguage() {
    return document.getElementsByClassName("languageButton-selected")[0].innerHTML;
}

function setNewLanguage(language) {
    const languageButtons = document.getElementsByClassName("languageButton");
    for (let i=0; i < languageButtons.length; i++) {
      const languageButton = languageButtons[i];
      if (languageButton.innerHTML === language) {
        languageButton.classList.add("languageButton-selected");
      } else {
        languageButton.classList.remove("languageButton-selected");
      }
    }

    languageExampleFunctions[language]();
}

/* When the user clicks on the button,
toggle between hiding and showing the dropdown content */
function showDropdown() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown menu if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

function addJavaScriptExamples() {
    const jsExamples = [
        "exS1764.example.js",
        "exS2583.example.js",
        "exS930.example.js",
        "exS1854.example.js",
        "exUnreachableCode.example.js",
        "exS2201.example.js",
        "exS3699.example.js",
        "exS2259.example.js",
        "exS3923.example.js"
    ];
    addLanguageExamples(jsExamples, "JavaScript");
}

function addJavaExamples() {
    addLanguageExamples(["ex.example1.java", "ex.example2.java"], "Java");
}

function addPythonExamples() {
    addLanguageExamples(["ex.example1.py", "ex.example2.py"], "Python");
}

function addPhpExamples() {
    addLanguageExamples(["ex.example1.php", "ex.example2.php"], "PHP");
}

function addLanguageExamples(exampleLinks, language, version) {
    const examplesDropdown = document.getElementById("myDropdown");
    examplesDropdown.innerHTML = "";

    exampleLinks.forEach((element, index) => {
        examplesDropdown.appendChild(createExample("#/" + language + "/latest/" + element, index + 1));
    });
}

function createExample(linkToExample, number) {
    const link = document.createElement("a");
    link.appendChild(document.createTextNode("Example #" + number));
    link.setAttribute("href", linkToExample)
    return link;
}

function updateLocationHash(store) {
    var languageSelected = document.getElementsByClassName("languageButton-selected")[0];
    var language = languageSelected.innerHTML;

    var newPath = "#/" + language;
    if (languageVersion !== "latest" || storedAs !== "") {
        newPath += "/" + languageVersion;
        if (storedAs !== "") {
            newPath += "/" + storedAs;
        }
    }

    if (document.location.hash !== newPath) {
        document.location.hash = newPath;
    }
    if (store) {
        window.prompt("", window.location.href);
    }
}

function getCode() {
    return document.getElementById("input").value;
}

function setCode(code) {
    document.getElementById("input").value = code;
}

function analyze(store) {
    document.getElementById("output").style.opacity = 0.5;
    var loc = window.location;
    var baseUrl = loc.protocol + "//" + loc.hostname + (loc.port? ":"+loc.port : "") + "/" + loc.pathname.split('/')[1];
    var url = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "analyze";
    var method = "POST";
    var async = true;
    var request = new XMLHttpRequest();
    request.onerror = function () {
        document.getElementById("output").innerHTML = '<div class="issue">Connection problem</div>';
        document.getElementById("output").style.opacity = 1;
    };
    request.onload = function () {

        var response = JSON.parse(request.responseText);

        if (response.exception) {
            document.getElementById("output").innerHTML = '<div class="issue">Internal server error</div>';
        } else {
            document.getElementById("output").innerHTML = doFormat(response);
        }
        document.getElementById("output").style.opacity = 1;

        if (storedAs !== "" || store) {
            setCode(response.code);
            if (response.storedAs) {
                storedAs = response.storedAs;
                languageVersion = response.languageVersion;
            } else {
                storedAs = "";
            }
        }
        updateLocationHash(store);
    }

    var language = getCurrentLanguage();
    var code = getCode();

    if (code === "" && storedAs === "") {
        document.getElementById("output").innerHTML = '';
    } else {
        request.open(method, url, async);
        request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var params = "language=" + encodeURIComponent(language) + "&languageVersion=" + encodeURIComponent(languageVersion);
        if (storedAs !== "") {
            params += "&restore=" + storedAs;
        } else {
            if (store) {
                params += "&store=true";
            }
            params += "&code=" + encodeURIComponent(code);
        }
        request.send(params);
        document.getElementById("output").style.opacity = 0.7;
    }
}

function onLanguageClick(language) {
    setNewLanguage(language);
    onLanguageChange();
}

