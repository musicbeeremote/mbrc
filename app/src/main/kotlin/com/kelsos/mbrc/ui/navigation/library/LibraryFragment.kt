package com.kelsos.mbrc.ui.navigation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.snackbar
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class LibraryFragment : Fragment(),
  LibraryView,
  OnQueryTextListener,
  OnPageChangeListener {

  private val pager: ViewPager by bindView(R.id.search_pager)
  private val tabs: TabLayout by bindView(R.id.pager_tab_strip)

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private var albumArtistOnly: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null

  private lateinit var scope: Scope
  @Inject
  lateinit var presenter: LibraryPresenter

  private var refreshDialog: SyncProgressDialog? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    if (!query.isEmpty() && query.trim { it <= ' ' }.isNotEmpty()) {
      closeSearch()

      //SearchResultsActivity.start(this, query.trim { it <= ' ' })
      //navigate
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
        return true
      }
    }
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(PRESENTER_SCOPE).installModules(LibraryModule())
    scope = Toothpick.openScopes(requireActivity().application, PRESENTER_SCOPE, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    setHasOptionsMenu(true)
    return inflater.inflate(R.layout.fragment_library, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    pagerAdapter = LibraryPagerAdapter(requireActivity())

    pager.adapter = pagerAdapter
    pager.offscreenPageLimit = 4
    pager.addOnPageChangeListener(this)

    tabs.setupWithViewPager(pager)

    presenter.attach(this)
  }


  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater?.inflate(R.menu.library_search, menu)
    searchMenuItem = menu?.findItem(R.id.library_screen__action_search)?.apply {
      searchView = actionView as SearchView
    }

    albumArtistOnly = menu?.findItem(R.id.library_album_artist)

    searchView?.apply {
      queryHint = getString(R.string.library_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@LibraryFragment)
    }

    presenter.loadArtistPreference()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.library_screen__action_refresh) {
      presenter.refresh()
      return true
    } else if (item?.itemId == R.id.library_album_artist) {
      albumArtistOnly?.let {
        it.isChecked = !it.isChecked
        presenter.setArtistPreference(it.isChecked)
      }

      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    presenter.detach()
    pagerAdapter = null
    Toothpick.closeScope(this)
    Toothpick.closeScope(PRESENTER_SCOPE)

    super.onDestroy()
  }

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
  }

  override fun onPageSelected(position: Int) {
  }

  override fun onPageScrollStateChanged(state: Int) {
  }

  override fun updateArtistOnlyPreference(albumArtistOnly: Boolean?) {
    this.albumArtistOnly?.isChecked = albumArtistOnly ?: false
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(PAGER_POSITION, pager.currentItem)
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    pager.currentItem = savedInstanceState?.getInt(PAGER_POSITION, 0) ?: 0
  }

  override fun refreshFailed() {
    snackbar(R.string.refresh_failed)
  }

  override fun showRefreshing() {
    refreshDialog = syncDialog()
    refreshDialog?.show()
  }

  override fun updateSyncProgress(progress: SyncProgress) {
    refreshDialog?.updateProgress(progress)
  }

  override fun hideRefreshing() {
    refreshDialog?.dismiss()
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private const val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}