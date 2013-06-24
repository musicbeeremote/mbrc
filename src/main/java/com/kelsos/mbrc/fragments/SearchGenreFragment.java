package com.kelsos.mbrc.fragments;


import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreEntryAdapter;
import com.kelsos.mbrc.data.GenreEntry;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;

public class SearchGenreFragment extends RoboSherlockListFragment {
    private static final int QUEUE_NEXT = 1;
    private static final int QUEUE_LAST = 2;
    private static final int PLAY_NOW = 3;
    private static final int GET_SUB = 4;
    private GenreEntryAdapter adapter;
    @Inject Bus bus;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
        return view;
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, QUEUE_NEXT, 0, "Queue Next");
        menu.add(0, QUEUE_LAST, 0, "Queue Last");
        menu.add(0, PLAY_NOW, 0, "Play Now");
        menu.add(0, GET_SUB, 0, "Get Artists");
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = adapter.getItem(mi.position);
            String qContext = Protocol.LibraryQueueGenre;
            String gSub = Protocol.LibraryGenreArtists;
            String query = ((GenreEntry) line).getName();

            UserAction ua = null;
            switch (item.getItemId()) {
                case QUEUE_NEXT:
                    ua = new UserAction(qContext, new Queue("next",query));
                    break;
                case QUEUE_LAST:
                    ua = new UserAction(qContext, new Queue("last",query));
                    break;
                case PLAY_NOW:
                    ua = new UserAction(qContext, new Queue("now", query));
                    break;
                case GET_SUB:
                    ua = new UserAction(gSub, query);
                    break;
            }

            if (ua != null) bus.post(new MessageEvent(ProtocolEvent.UserAction, ua));
            return true;
        } else {
            return false;
        }

    }

    public void updateAdapter(GenreEntryAdapter adapter) {
        this.adapter = adapter;
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
