package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.fragments.SimpleLibrarySearchFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ShowArtistSearchResults implements ICommand {
    @Inject
    ActiveFragmentProvider afProvider;
    @Override
    public void execute(IEvent e) {
        if(afProvider.getActiveFragment(SimpleLibrarySearchFragment.class)!=null)
        {
            ArrayList<ArtistEntry> artists = new ArrayList<ArtistEntry>();
            JsonNode node = (JsonNode)e.getData();


            Activity cActivity = afProvider.getActiveFragment(SimpleLibrarySearchFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    SimpleLibrarySearchFragment fragment = ((SimpleLibrarySearchFragment)afProvider.getActiveFragment(MainFragment.class));

                }
            });
        }
    }
}
