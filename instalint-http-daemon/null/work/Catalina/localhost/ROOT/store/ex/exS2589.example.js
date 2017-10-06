function initConnection() {
    var transport = $.cookie('jabbr.transport')
                    || ['webSockets', 'serverSentEvents', 'longPolling'],
    var options = {};

    if (transport) {
      options.transport = transport;
    }

    initConnection(options);
}
