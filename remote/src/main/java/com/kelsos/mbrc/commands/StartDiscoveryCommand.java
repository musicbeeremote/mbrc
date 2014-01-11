package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.ServiceDiscovery;

public class StartDiscoveryCommand implements ICommand {
    private ServiceDiscovery mDiscovery;

    @Inject public StartDiscoveryCommand(ServiceDiscovery mDiscovery) {
        this.mDiscovery = mDiscovery;
    }

    @Override public void execute(IEvent e) {
        mDiscovery.startDiscovery();
    }
}
