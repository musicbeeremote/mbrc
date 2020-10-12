package com.kelsos.mbrc.ui.navigation.library

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.core.view.MenuItemCompat
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LibraryPagerAdapter
import com.kelsos.mbrc.annotations.Search
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultsActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LibraryActivity : BaseActivity(),
    LibraryView,
    OnQueryTextListener {

  @BindView(R.id.search_pager) lateinit var pager: ViewPager2
  @BindView(R.id.pager_tab_strip) lateinit var tabs: TabLayout

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private var albumArtistOnly: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null
  private var scope: Scope? = null
  @Inject lateinit var presenter: LibraryPresenter

  private var refreshDialog: MaterialDialog? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    if (!TextUtils.isEmpty(query) && query.trim { it <= ' ' }.isNotEmpty()) {
      closeSearch()

      val searchIntent = Intent(this, SearchResultsActivity::class.java)
      searchIntent.putExtra(SearchResultsActivity.QUERY, query.trim { it <= ' ' })
      startActivity(searchIntent)
    }

    return true
  }

  private fun closeSearch(): Boolean {
    searchView?.let {
      if (it.isShown) {
        it.isIconified = true
        it.isFocusable = false
        it.clearFocus()
        it.setQuery("", false)
        searchMenuItem?.collapseActionView()
        MenuItemCompat.collapseActionView(searchMenuItem)
        return true
      }
    }
    return false
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
    pager.apply {
      adapter = pagerAdapter
    }

    TabLayoutMediator(tabs, pager) { currentTab, currentPosition ->
      currentTab.text = when(currentPosition) {
        Search.SECTION_ALBUM -> getString(R.string.label_albums)
        Search.SECTION_ARTIST -> getString(R.string.label_artists)
        Search.SECTION_GENRE -> getString(R.string.label_genres)
        Search.SECTION_TRACK -> getString(R.string.label_tracks)
        else -> throw IllegalArgumentException("invalid position")
      }
    }.attach()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.library_search, menu)
    searchMenuItem = menu.findItem(R.id.library_search_item)
    albumArtistOnly = menu.findItem(R.id.library_album_artist)
    searchView = MenuItemCompat.getActionView(searchMenuItem) as SearchView
    searchView!!.queryHint = getString(R.string.library_search_hint)
    searchView!!.setIconifiedByDefault(true)
    searchView!!.setOnQueryTextListener(this)
    presenter.loadArtistPreference()
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.library_refresh_item) {
      presenter.refresh()
      return true
    } else if (item.itemId == R.id.library_album_artist) {
      albumArtistOnly?.let {
        it.isChecked = !it.isChecked
        presenter.setArtistPreference(it.isChecked)
      }

      return true
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

  override fun onBackPressed() {
    if (closeSearch()) {
      return
    }
    super.onBackPressed()
  }

  override fun updateArtistOnlyPreference(albumArtistOnly: Boolean?) {
    this.albumArtistOnly?.isChecked = albumArtistOnly ?: false
  }

  override fun active(): Int {
    return R.id.nav_library
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
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
