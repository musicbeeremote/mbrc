package com.kelsos.mbrc.commands;

import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import roboguice.util.Ln;

public class ProtocolPongHandle implements ICommand {
  @Override public void execute(IEvent e) {
    Ln.d(e.getData());
  }
}
