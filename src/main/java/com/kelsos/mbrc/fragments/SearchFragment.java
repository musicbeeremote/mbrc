package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.*;
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
import com.viewpagerindicator.TitlePageIndicator;

public class SearchFragment extends RoboSherlockFragment implements SearchView.OnQueryTextListener{

    @Inject Bus bus;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private ViewPager mPager;

    @Override
    public boolean onQueryTextSubmit(String query) {
        String pContext = "";
        int current = mPager.getCurrentItem();

        switch (current) {
            case 0:
                pContext = Protocol.LibrarySearchGenre;
                break;
            case 1:
                pContext = Protocol.LibrarySearchArtist;
                break;
            case 2:
                pContext = Protocol.LibrarySearchAlbum;
                break;
            case 3:
                pContext = Protocol.LibrarySearchTitle;
                break;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_search, container, false);
        mPager = (ViewPager)view.findViewById(R.id.search_pager);
        mPager.setAdapter(new SearchPagerAdapter(getActivity()));
        mPager.setOffscreenPageLimit(3);
        TitlePageIndicator titleIndicator = (TitlePageIndicator)view.findViewById(R.id.search_categories);
        titleIndicator.setViewPager(mPager);
        return view;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        mSearchView = new SearchView(((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext());
        //mSearchView.setQueryHint("Search for " );
        mSearchView.setIconifiedByDefault(true);

        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchItem.setActionView(mSearchView);
        mSearchView.setOnQueryTextListener(this);
    }

    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        ArtistEntryAdapter adapter = new ArtistEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        mPager.setCurrentItem(1);
        ((SearchArtistFragment)((SearchPagerAdapter)mPager.getAdapter()).getFragment(1)).updateAdapter(adapter);
    }

    @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
        GenreEntryAdapter adapter = new GenreEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        mPager.setCurrentItem(0);
        ((SearchGenreFragment)((SearchPagerAdapter)mPager.getAdapter()).getFragment(0)).updateAdapter(adapter);
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
        AlbumEntryAdapter adapter = new AlbumEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        mPager.setCurrentItem(2);
        ((SearchAlbumFragment)((SearchPagerAdapter)mPager.getAdapter()).getFragment(2)).updateAdapter(adapter);
    }

    @Subscribe public void handleTrackResults(TrackSearchResults results) {
        TrackEntryAdapter adapter = new TrackEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        mPager.setCurrentItem(3);
        ((SearchTrackFragment)((SearchPagerAdapter)mPager.getAdapter()).getFragment(3)).updateAdapter(adapter);
    }
}
