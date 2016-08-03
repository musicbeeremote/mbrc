package com.kelsos.mbrc.commands;

import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ServiceDiscovery;

public class StartDiscoveryCommand implements ICommand {
  private ServiceDiscovery mDiscovery;

  @Inject public StartDiscoveryCommand(ServiceDiscovery mDiscovery) {
    this.mDiscovery = mDiscovery;
  }

  @Override public void execute(IEvent e) {
    mDiscovery.startDiscovery();
  }
}
