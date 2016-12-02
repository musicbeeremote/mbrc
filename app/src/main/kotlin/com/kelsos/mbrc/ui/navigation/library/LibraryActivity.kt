package com.kelsos.mbrc.ui.navigation.library

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LibraryPagerAdapter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultsActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LibraryActivity : BaseActivity(),
                        LibraryView,
                        OnQueryTextListener,
                        OnPageChangeListener {

  @BindView(R.id.search_pager) lateinit var pager: ViewPager
  @BindView(R.id.pager_tab_strip) lateinit var tabs: TabLayout

  private var mSearchView: SearchView? = null
  private var searchView: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null
  private var scope: Scope? = null
  @Inject lateinit var presenter: LibraryPresenter

  private var refreshDialog: MaterialDialog? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    if (!TextUtils.isEmpty(query) && query.trim { it <= ' ' }.isNotEmpty()) {
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
    scope!!.installModules(SmoothieActivityModule(this), LibraryModule())
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

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.library_refresh_item) {
      presenter.refresh()
    }
    return super.onOptionsItemSelected(item)
  }

  public override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
    pagerAdapter = null
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
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

  override fun refreshFailed() {
    Snackbar.make(pager, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun showRefreshing() {
    refreshDialog = MaterialDialog.Builder(this)
        .content(R.string.refreshing_library_data)
        .progress(true, 100, false)
        .cancelable(false)
        .build()

    refreshDialog?.show()
  }

  override fun hideRefreshing() {
    refreshDialog?.dismiss()
  }

  companion object {
    private val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
  }
}
