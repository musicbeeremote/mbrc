package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.SearchPagerAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.ClearCachedSearchResults;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.SearchScrollChanged;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import roboguice.fragment.RoboFragment;

public class SearchFragment extends RoboFragment implements SearchView.OnQueryTextListener,
    ViewPager.OnPageChangeListener {

  @Inject Bus bus;
  @Bind(R.id.search_pager) ViewPager mPager;
  @Bind(R.id.pager_tab_strip) TabLayout tabs;
  @Bind(R.id.search_clear_fab) FloatingActionButton fab;

  private SearchView mSearchView;
  private MenuItem mSearchItem;
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
      default:
        break;
    }

    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(mSearchItem);
    bus.post(
        new MessageEvent(ProtocolEventType.UserAction, new UserAction(pContext, query.trim())));

    return false;
  }

  @OnClick(R.id.search_clear_fab) public void onFabClick(View view) {
    int current = mPager.getCurrentItem();
    // Not the most elegant but the fastest way to do it at this point
    //noinspection ResourceType
    bus.post(new ClearCachedSearchResults(current));
    switch (current) {
      case 0:
        bus.post(new GenreSearchResults(new ArrayList<>(), true));
        break;
      case 1:
        bus.post(new ArtistSearchResults(new ArrayList<>(), true));
        break;
      case 2:
        bus.post(new AlbumSearchResults(new ArrayList<>(), true));
        break;
      case 3:
        bus.post(new TrackSearchResults(new ArrayList<>(), true));
        break;
      default:
        break;
    }
  }

  @Override public boolean onQueryTextChange(String newText) {
    return false;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_search, container, false);
    ButterKnife.bind(this, view);
    mPager.setAdapter(mAdapter);
    tabs.setupWithViewPager(mPager);
    mPager.addOnPageChangeListener(this);
    return view;
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
  }

  @Override public void onStop() {
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

  @Subscribe public void handleScrollChange(SearchScrollChanged event) {
    if (event.isScrollingUpwards()) {
      fab.hide();
    } else {
      fab.show();
    }
  }



  @Override public void onDestroy() {
    super.onDestroy();
    mAdapter = null;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override public void onPageSelected(int position) {
    if (!fab.isShown()) {
      fab.show();
    }
  }

  @Override public void onPageScrollStateChanged(int state) {

  }
}
