package com.kelsos.mbrc.commands;

import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import timber.log.Timber;

public class ProtocolPongHandle implements ICommand {
  @Override public void execute(IEvent e) {
    Timber.d(e.getData().toString());
  }
}
