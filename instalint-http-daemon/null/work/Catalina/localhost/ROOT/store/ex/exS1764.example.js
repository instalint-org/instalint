// Identical expressions should not be used
// on both sides of a binary operator

function updateOptions(newOptions) {
    let renderSideBySideChanged = false;
    if (typeof newOptions.renderSideBySide !== 'undefined') {
        if (this.oldOptions._renderSideBySide() === this.oldOptions._renderSideBySide()) {
            this._renderSideBySide = newOptions.renderSideBySide;
            renderSideBySideChanged = true;
        }
    }

    return renderSideBySideChanged;
}


