package com.kelsos.mbrc.commands;

import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.ProtocolHandler;

public class SocketDataAvailableCommand implements ICommand {
    private ProtocolHandler handler;

    @Inject public SocketDataAvailableCommand(ProtocolHandler handler) {
        this.handler = handler;
    }

    public void execute(final IEvent e) {
        try {
            handler.preProcessIncoming(e.getDataString());
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "message processing", ex);
            }
        }

    }
}
