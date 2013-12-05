package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.*;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.HashMap;
import java.util.Map;

public class BrowseFragment extends BaseFragment {
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private ViewPager mPager;
    private BrowsePagerAdapter mAdapter;

//    @Override public boolean onQueryTextSubmit(String query) {
//        String pContext = "";
//        int current = mPager.getCurrentItem();
//
//        switch (current) {
//            case 0:
//                pContext = Protocol.LibrarySearchGenre;
//                break;
//            case 1:
//                pContext = Protocol.LibrarySearchArtist;
//                break;
//            case 2:
//                pContext = Protocol.LibrarySearchAlbum;
//                break;
//            case 3:
//                pContext = Protocol.LibrarySearchTitle;
//                break;
//        }
//
//        mSearchView.setIconified(true);
//        mSearchItem.collapseActionView();
//
//        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(pContext, query.trim())));
//
//        return false;
//    }
//
//    @Override public boolean onQueryTextChange(String newText) {
//        return false;
//    }


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_search, container, false);
        mPager = (ViewPager) view.findViewById(R.id.search_pager);
        mPager.setAdapter(mAdapter);
        TitlePageIndicator titleIndicator = (TitlePageIndicator) view.findViewById(R.id.search_categories);
        titleIndicator.setViewPager(mPager);
        return view;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Map<String, String> req = new HashMap<String,String>();
                req.put("type","full");
                getBus().post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, req)));
                break;
        }
        return false;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new BrowsePagerAdapter(getActivity());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        mSearchView = new SearchView(((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext());
        //mSearchView.setQueryHint("Search for " );
//        mSearchView.setIconifiedByDefault(true);

//        inflater.inflate(R.menu.menu_now_playing, menu);
        menu.add(15,1,0,"Sync Library");
//        mSearchItem = menu.findItem(R.id.now_playing_search_item);
//        mSearchItem.setActionView(mSearchView);
        //mSearchView.setOnQueryTextListener(this);
    }

    @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(0);
            if (results.getList().size() == 0) {
                getBus().post(new NotifyUser(getString(R.string.search_msg_genre_not_found)));
            }
        }
    }

    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(1);
            if (results.getList().size() == 0) {
                getBus().post(new NotifyUser(getString(R.string.search_msg_artist_not_found)));
            }
        }
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(2);
            if (results.getList().size() == 0) {
                getBus().post(new NotifyUser(getString(R.string.search_msg_album_not_found)));
            }
        }
    }

    @Subscribe public void handleTrackResults(TrackSearchResults results) {
        if (!results.isStored()) {
            mPager.setCurrentItem(3);
            if (results.getList().size() == 0) {
                getBus().post(new NotifyUser(getString(R.string.search_msg_track_not_found)));
            }
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }
}
