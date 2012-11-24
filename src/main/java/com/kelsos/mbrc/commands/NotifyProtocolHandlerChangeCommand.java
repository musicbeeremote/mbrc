package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class NotifyProtocolHandlerChangeCommand implements ICommand {
    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    ProtocolHandler pHandler;

    @Override
    public void execute(IEvent e) {
        if(afProvider.getActiveFragment(NowPlayingFragment.class)!=null){
            pHandler.requestAction(ProtocolHandler.PlayerAction.Playlist);
        }
    }
}
