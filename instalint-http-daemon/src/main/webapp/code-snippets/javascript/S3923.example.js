const FIELD = opt_isBlended ? new goog.editor.SeamlessField('testField') :
  new goog.editor.SeamlessField('testField');

goog.ui.PopupColorPicker.prototype.detach = function(element) {
  if (this.showOnHover_) {
    this.getHandler().unlisten(
        element, goog.events.EventType.MOUSEOVER, this.show_);
  } else {
    this.getHandler().unlisten(
        element, goog.events.EventType.MOUSEOVER, this.show_);
  }
}
