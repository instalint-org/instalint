// Jump statements should not be followed by other statements

xhr.onload = function () {
    try {
        if (xhr.readyState === 4 && xhr.status >= 400 && xhr.status <= 599) { // Handle HTTP status codes of 4xx and 5xx as errors, even if xhr.onerror was not called.
            return onerror.call(_this, file, xhr);
        }
        else {
            return onload.call(_this, file, xhr);
        }
        return onload.call(_this, file, xhr);
    } catch (e) {
        _this.asyncComplete(file, e.message || 'Exception');
    }
};
