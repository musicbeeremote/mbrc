package com.kelsos.mbrc.ui.navigation.library

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LibraryPagerAdapter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultsActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule

class LibraryActivity : BaseActivity(), SearchView.OnQueryTextListener, ViewPager.OnPageChangeListener {

  @BindView(R.id.search_pager) lateinit var pager: ViewPager
  @BindView(R.id.pager_tab_strip) lateinit var tabs: TabLayout

  private var mSearchView: SearchView? = null
  private var searchView: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null
  private var scope: Scope? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    if (!TextUtils.isEmpty(query) && query.trim { it <= ' ' }.length > 0) {
      val searchIntent = Intent(this, SearchResultsActivity::class.java)
      searchIntent.putExtra(SearchResultsActivity.QUERY, query.trim { it <= ' ' })
      startActivity(searchIntent)
    }
    mSearchView!!.isIconified = true
    MenuItemCompat.collapseActionView(searchView)
    return true
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return false
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_library)
    ButterKnife.bind(this)
    super.setup()
    pagerAdapter = LibraryPagerAdapter(this)
    pager.adapter = pagerAdapter
    tabs.setupWithViewPager(pager)
    pager.addOnPageChangeListener(this)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.library_search, menu)
    searchView = menu.findItem(R.id.library_search_item)
    mSearchView = MenuItemCompat.getActionView(searchView) as SearchView
    mSearchView!!.queryHint = getString(R.string.library_search_hint)
    mSearchView!!.setIconifiedByDefault(true)
    mSearchView!!.setOnQueryTextListener(this)
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
    pagerAdapter = null
  }

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

  }

  override fun onPageSelected(position: Int) {

  }

  override fun onPageScrollStateChanged(state: Int) {

  }

  override fun active(): Int {
    return R.id.nav_library
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(PAGER_POSITION, pager.currentItem)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    pager.currentItem = savedInstanceState.getInt(PAGER_POSITION, 0)
  }

  companion object {

    private val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
  }
}
