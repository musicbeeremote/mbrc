package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.SearchFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class VisualUpdateSimpleSearch implements ICommand {
    @Inject
    ActiveFragmentProvider afp;

    @Override
    public void execute(final IEvent e) {
        if(afp.getActiveFragment(SearchFragment.class) != null) {
            Activity cActivity = afp.getActiveFragment(SearchFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                   // ((SimpleLibrarySearchFragment) afp.getActiveFragment(SimpleLibrarySearchFragment.class)).updateArtistResults(((MessageEvent)e).getList());
                }
            });
        }
    }
}
