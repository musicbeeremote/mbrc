package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumEntryAdapter;
import com.kelsos.mbrc.adapters.ArtistEntryAdapter;
import com.kelsos.mbrc.adapters.GenreEntryAdapter;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.data.*;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class SearchFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener, ActionBar.OnNavigationListener {

    private static final int QUEUE_NEXT = 1;
    private static final int QUEUE_LAST = 2;
    private static final int PLAY_NOW = 3;
    private static final int GET_SUB = 4;

    @Inject
    Bus bus;
    private String[] mSearchOptions;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private String mCurrentFiltering;
    private ArrayAdapter<?> adapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        String pContext = "";
        if (mCurrentFiltering.equals(mSearchOptions[0])) {
            pContext = Protocol.LibrarySearchArtist;
        } else if (mCurrentFiltering.equals(mSearchOptions[1])) {
            pContext = Protocol.LibrarySearchAlbum;
        } else if (mCurrentFiltering.equals(mSearchOptions[2])) {
            pContext = Protocol.LibrarySearchTitle;
        } else if (mCurrentFiltering.equals(mSearchOptions[3])) {
            pContext = Protocol.LibrarySearchGenre;
        }

        mSearchView.setIconified(true);
        mSearchItem.collapseActionView();

        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(pContext, query.trim())));

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mCurrentFiltering = mSearchOptions[itemPosition];
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_list_simple, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        mSearchView = new SearchView(((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
        mSearchView.setIconifiedByDefault(true);

        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchItem.setActionView(mSearchView);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(this.getListView());
        mSearchOptions = getResources().getStringArray(R.array.library_search_options);
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.library_search_options, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);


        ((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().setListNavigationCallbacks(list, this);
    }

    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        adapter = new ArtistEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        setListAdapter(adapter);
    }

    @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
        adapter = new GenreEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        setListAdapter(adapter);
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
        adapter = new AlbumEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        setListAdapter(adapter);
    }

    @Subscribe public void handleTrackResults(TrackSearchResults results) {
        adapter = new TrackEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(0, QUEUE_NEXT, 0, "Queue Next");
        menu.add(0, QUEUE_LAST, 0, "Queue Last");
        menu.add(0, PLAY_NOW, 0, "Play Now");


        String type = null;

        if (mCurrentFiltering.equals("Artist")) {
            type = "Albums";
        } else if (mCurrentFiltering.equals("Album")) {
            type = "Tracks";
        } else if (mCurrentFiltering.equals("Genre")) {
            type = "Artists";
        }

        if (type!=null) {
            menu.add(0, GET_SUB, 0, "Get " + type);
        }

        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;

        //menu.setHeaderTitle();
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Object line = adapter.getItem(mi.position);
        String qContext = "";
        String gSub = "";
        String query = "";

        Log.d("dd",line.toString());

        if (mCurrentFiltering.equals(mSearchOptions[0])) {
            qContext = Protocol.LibraryQueueArtist;
            gSub = Protocol.LibraryArtistAlbums;
            query = ((ArtistEntry)line).getArtist();
        } else if (mCurrentFiltering.equals(mSearchOptions[1])) {
            qContext = Protocol.LibraryQueueAlbum;
            gSub = Protocol.LibraryAlbumTracks;
            query = ((AlbumEntry)line).getAlbum();
        } else if (mCurrentFiltering.equals(mSearchOptions[2])) {
            qContext = Protocol.LibraryQueueTrack;
            query = ((TrackEntry)line).getTitle();
        } else if (mCurrentFiltering.equals(mSearchOptions[3])) {
            qContext = Protocol.LibraryQueueGenre;
            gSub = Protocol.LibraryGenreArtists;
            query = ((GenreEntry)line).getName();
        }

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
        return super.onContextItemSelected(item);
    }
}
