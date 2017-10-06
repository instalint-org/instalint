// Dead stores should be removed

function download(urlInfo, downloadId) {
    var d = new $.Deferred();
    var filename = urlInfo.filenameHint;
    filename = filename.replace(/[^a-zA-Z0-9_\- \(\)\.]/g, "_");
    if (!filename) {
        filename = "extension.zip";
    }
    var r = extensionManager.downloadFile(downloadId, urlInfo.url, PreferencesManager.get("proxy"));
    return d.promise(r);
}


function applyPlacement(offset, placement){
  var $tip = this.tip()
    , width = $tip[0].offsetWidth
    , height = $tip[0].offsetHeight
    , actualWidth
    , actualHeight
    , delta
    , replace

  actualWidth = $tip[0].offsetWidth
  actualHeight = $tip[0].offsetHeight

  if (placement === 'top' && actualHeight !== height) {
    offset.top = offset.top + height - actualHeight
    replace = true
  }

  if (placement === 'bottom' || placement === 'top') {
    delta = 0
    if (offset.left < 0){
      delta = offset.left * -2
      offset.left = 0
      $tip.offset(offset)
      actualWidth = $tip[0].offsetWidth
      actualHeight = $tip[0].offsetHeight
    }
    this.replaceArrow(delta - width + actualWidth, actualWidth, 'left')

  } else {
    this.replaceArrow(actualHeight - height, actualHeight, 'top')
  }

  if (replace) $tip.offset(offset)
}
