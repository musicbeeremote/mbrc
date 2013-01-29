package com.kelsos.mbrc.commands;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.fragments.LibraryArtistsFragment;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class AllArtistsAvailable implements ICommand {
    @Inject
    ActiveFragmentProvider afProvider;


    public void execute(final IEvent e)
    {
        if(afProvider.getActiveFragment(LibraryArtistsFragment.class)!=null)
        {
            Activity cActivity = afProvider.getActiveFragment(LibraryArtistsFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    LibraryArtistsFragment fragment = ((LibraryArtistsFragment)afProvider.getActiveFragment(LibraryArtistsFragment.class));
                    fragment.updateListData(((ProtocolDataEvent)e).getList());
                }
            });
        }
    }
}
