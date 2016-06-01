package com.kelsos.mbrc.ui.activities.nav;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LibraryPagerAdapter;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.SearchScrollChanged;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.activities.SearchResultsActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;

public class LibraryActivity extends BaseActivity implements SearchView.OnQueryTextListener,
    ViewPager.OnPageChangeListener {

  @Inject Bus bus;
  @BindView(R.id.search_pager) ViewPager pager;
  @BindView(R.id.pager_tab_strip) TabLayout tabs;


  private SearchView mSearchView;
  private MenuItem searchView;
  private LibraryPagerAdapter pagerAdapter;

  @Override public boolean onQueryTextSubmit(String query) {
    if (!TextUtils.isEmpty(query) && query.trim().length() > 0) {
      Intent searchIntent = new Intent(this, SearchResultsActivity.class);
      searchIntent.putExtra(SearchResultsActivity.QUERY, query.trim());
      startActivity(searchIntent);
    }
    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(searchView);
    return true;
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
    setContentView(R.layout.activity_library);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    super.setup();
    pagerAdapter = new LibraryPagerAdapter(this);
    pager.setAdapter(pagerAdapter);
    tabs.setupWithViewPager(pager);
    pager.addOnPageChangeListener(this);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_now_playing, menu);
    searchView = menu.findItem(R.id.now_playing_search_item);
    mSearchView = (SearchView) MenuItemCompat.getActionView(searchView);
    mSearchView.setQueryHint(getString(R.string.library_search_hint));
    mSearchView.setIconifiedByDefault(true);
    mSearchView.setOnQueryTextListener(this);
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
