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
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LibraryPagerAdapter;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.activities.SearchResultsActivity;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class LibraryActivity extends BaseActivity
    implements SearchView.OnQueryTextListener, ViewPager.OnPageChangeListener {

  @Inject RxBus bus;
  @BindView(R.id.search_pager) ViewPager pager;
  @BindView(R.id.pager_tab_strip) TabLayout tabs;

  private SearchView mSearchView;
  private MenuItem searchView;
  private LibraryPagerAdapter pagerAdapter;
  private Scope scope;

  @Override
  public boolean onQueryTextSubmit(String query) {
    if (!TextUtils.isEmpty(query) && query.trim().length() > 0) {
      Intent searchIntent = new Intent(this, SearchResultsActivity.class);
      searchIntent.putExtra(SearchResultsActivity.QUERY, query.trim());
      startActivity(searchIntent);
    }
    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(searchView);
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return false;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_library);
    ButterKnife.bind(this);
    super.setup();
    pagerAdapter = new LibraryPagerAdapter(this);
    pager.setAdapter(pagerAdapter);
    tabs.setupWithViewPager(pager);
    pager.addOnPageChangeListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.library_search, menu);
    searchView = menu.findItem(R.id.library_search_item);
    mSearchView = (SearchView) MenuItemCompat.getActionView(searchView);
    mSearchView.setQueryHint(getString(R.string.library_search_hint));
    mSearchView.setIconifiedByDefault(true);
    mSearchView.setOnQueryTextListener(this);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
    pagerAdapter = null;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {

  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  @Override
  protected int active() {
    return R.id.nav_library;
  }
}
