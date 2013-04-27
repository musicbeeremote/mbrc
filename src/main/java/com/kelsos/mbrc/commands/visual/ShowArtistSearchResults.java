package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.fragments.SearchFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

import java.util.ArrayList;

public class ShowArtistSearchResults implements ICommand {
    @Inject
    ActiveFragmentProvider afProvider;
    @Override
    public void execute(IEvent e) {
        if(afProvider.getActiveFragment(SearchFragment.class)!=null)
        {
            ArrayList<ArtistEntry> artists = new ArrayList<ArtistEntry>();
            ArrayNode node = (ArrayNode)e.getData();
            for(int i = 0; i < node.size(); i++) {
                JsonNode jNode = node.get(i);
                ArtistEntry entry = new ArtistEntry(jNode);
                artists.add(entry);
            }

            final ArrayList<ArtistEntry> fi = artists;

            Activity cActivity = afProvider.getActiveFragment(SearchFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    SearchFragment fragment = ((SearchFragment)afProvider.getActiveFragment(SearchFragment.class));
                    fragment.updateListData(fi);

                }
            });
        }
    }
}
