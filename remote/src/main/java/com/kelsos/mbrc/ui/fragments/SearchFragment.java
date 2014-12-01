package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.*;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.SearchPagerAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.TitlePageIndicator;
import roboguice.fragment.RoboFragment;

public class SearchFragment extends RoboFragment implements SearchView.OnQueryTextListener {

    @Inject Bus bus;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private ViewPager mPager;
    private SearchPagerAdapter mAdapter;

    @Override public boolean onQueryTextSubmit(String query) {
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
        MenuItemCompat.collapseActionView(mSearchItem);
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(pContext, query.trim())));

        return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_search, container, false);
        mPager = (ViewPager) view.findViewById(R.id.search_pager);
        mPager.setAdapter(mAdapter);
        TitlePageIndicator titleIndicator = (TitlePageIndicator) view.findViewById(R.id.search_categories);
        titleIndicator.setViewPager(mPager);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new SearchPagerAdapter(getActivity());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        //mSearchView.setQueryHint("Search for " );
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
    }

    @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(0);
            if (results.getList().size() == 0) {
                bus.post(new NotifyUser(getString(R.string.search_msg_genre_not_found)));
            }
        }
    }

    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(1);
            if (results.getList().size() == 0) {
                bus.post(new NotifyUser(getString(R.string.search_msg_artist_not_found)));
            }
        }
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(2);
            if (results.getList().size() == 0) {
                bus.post(new NotifyUser(getString(R.string.search_msg_album_not_found)));
            }
        }
    }

    @Subscribe public void handleTrackResults(TrackSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(3);
            if (results.getList().size() == 0) {
                bus.post(new NotifyUser(getString(R.string.search_msg_track_not_found)));
            }
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }
}
