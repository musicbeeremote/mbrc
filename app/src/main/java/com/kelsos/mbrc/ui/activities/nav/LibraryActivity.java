package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
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
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import roboguice.RoboGuice;

public class LibraryActivity extends BaseActivity implements SearchView.OnQueryTextListener,
    ViewPager.OnPageChangeListener {

  @Inject Bus bus;
  @BindView(R.id.search_pager) ViewPager pager;
  @BindView(R.id.pager_tab_strip) TabLayout tabs;


  private SearchView mSearchView;
  private MenuItem mSearchItem;
  private BrowsePagerAdapter pagerAdapter;

  @Override public boolean onQueryTextSubmit(String query) {
    String pContext = "";
    int current = pager.getCurrentItem();

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

  public void onFabClick(View view) {
    int current = pager.getCurrentItem();
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


  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browse);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    super.setup();
    pagerAdapter = new BrowsePagerAdapter(this);
    pager.setAdapter(pagerAdapter);
    tabs.setupWithViewPager(pager);
    pager.addOnPageChangeListener(this);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //inflater.inflate(R.menu.menu_now_playing, menu);
    //mSearchItem = menu.findItem(R.id.now_playing_search_item);
    //mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    ////mSearchView.setQueryHint("Search for " );
    //mSearchView.setIconifiedByDefault(true);
    //mSearchView.setOnQueryTextListener(this);
    return super.onCreateOptionsMenu(menu);
  }

  @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
    if (!results.isStored()) {
      pager.setCurrentItem(0);
      if (results.getList().size() == 0) {
        bus.post(new NotifyUser(getString(R.string.search_msg_genre_not_found)));
      }
    }
  }

  @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
    if (!results.isStored()) {
      pager.setCurrentItem(1);
      if (results.getList().size() == 0) {
        bus.post(new NotifyUser(getString(R.string.search_msg_artist_not_found)));
      }
    }
  }

  @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
    if (!results.isStored()) {
      pager.setCurrentItem(2);
      if (results.getList().size() == 0) {
        bus.post(new NotifyUser(getString(R.string.search_msg_album_not_found)));
      }
    }
  }

  @Subscribe public void handleTrackResults(TrackSearchResults results) {
    if (!results.isStored()) {
      pager.setCurrentItem(3);
      if (results.getList().size() == 0) {
        bus.post(new NotifyUser(getString(R.string.search_msg_track_not_found)));
      }
    }
  }

  @Subscribe public void handleScrollChange(SearchScrollChanged event) {

  }



  @Override public void onDestroy() {
    super.onDestroy();
    pagerAdapter = null;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override public void onPageSelected(int position) {

  }

  @Override public void onPageScrollStateChanged(int state) {

  }

  @Override
  protected int active() {
    return R.id.nav_library;
  }
}
