// The output of functions that don't return anything should not be used

function processState() {

    var checkState = function (e) {
        if (document.readyState === "complete" || (e && e.type === "DOMContentLoaded")) {
            document.removeEventListener("readystatechange", checkState, false);
            document.removeEventListener("DOMContentLoaded", checkState, false);
        }
    };

    var state = process(checkState());

    if (state) {
        return true;
    } else if (!this.isDomReadyListening) {
        oCanvas.isDomReadyListening = true;
        document.addEventListener("readystatechange", checkState, false);
        document.addEventListener("DOMContentLoaded", checkState, false);
        return false;
    }

    return null;
}
