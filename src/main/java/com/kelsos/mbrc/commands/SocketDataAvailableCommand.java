package com.kelsos.mbrc.commands;

import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class SocketDataAvailableCommand implements ICommand
{
	@Inject
	ProtocolHandler handler;

	public void execute(IEvent e)
	{
        try {
            handler.answerProcessor(e.getDataString());
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "message processing", ex);
            }
        }

	}
}
