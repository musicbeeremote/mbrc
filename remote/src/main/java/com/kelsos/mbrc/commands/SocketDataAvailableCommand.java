package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.ProtocolHandler;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

public class SocketDataAvailableCommand implements ICommand {
    private ProtocolHandler handler;
    private static final Logger logger = LoggerManager.getLogger();

    @Inject public SocketDataAvailableCommand(ProtocolHandler handler) {
        this.handler = handler;
    }

    public void execute(final IEvent e) {
        try {
            handler.preProcessIncoming(e.getDataString());
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                logger.e("message processing", ex);
            }
        }

    }
}
