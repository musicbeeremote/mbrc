package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.data.DataArrayAdapter;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;

import java.util.ArrayList;

public class SimpleLibrarySearchFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener, ActionBar.OnNavigationListener {

    @Inject
    Bus bus;
    @Inject
    ActiveFragmentProvider afProvider;

    private String[] mSearchOptions;

    private SearchView mSearchView;
    private MenuItem mSearchItem;

    private String mCurrentFiltering;
    private DataArrayAdapter adapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        String pContext = "";
        if(mCurrentFiltering.equals(mSearchOptions[0])){
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

        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(pContext,query)));

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ui_list_simple, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        afProvider.addActiveFragment(this);
    }

    @Override
    public void onPause()
    {
        afProvider.removeActiveFragment(this);
        super.onPause();
    }

    @Override
    public void onStop()
    {
        afProvider.removeActiveFragment(this);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        afProvider.removeActiveFragment(this);
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        afProvider.addActiveFragment(this);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){

        mSearchView = new SearchView(((RoboSherlockFragmentActivity)getActivity()).getSupportActionBar().getThemedContext());
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


        ((RoboSherlockFragmentActivity)getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ((RoboSherlockFragmentActivity)getActivity()).getSupportActionBar().setListNavigationCallbacks(list, this);
    }

    public void updateListData(ArrayList<ArtistEntry> list)
    {
        adapter = new DataArrayAdapter(getActivity(), R.layout.ui_list_track_item, list);
        setListAdapter(adapter);
    }
}
