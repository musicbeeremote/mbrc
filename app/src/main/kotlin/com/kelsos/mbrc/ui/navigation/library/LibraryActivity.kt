package com.kelsos.mbrc.ui.navigation.library

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isGone
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ActivityLibraryBinding
import com.kelsos.mbrc.databinding.LibraryStatsLayoutBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LibraryActivity :
  BaseNavigationActivity(),
  LibraryView,
  OnQueryTextListener {

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private var albumArtistOnly: MenuItem? = null
  private var searchClear: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null

  private lateinit var scope: Scope
  @Inject
  lateinit var presenter: LibraryPresenter
  private lateinit var binding: ActivityLibraryBinding

  override fun onQueryTextSubmit(query: String): Boolean {
    val search = query.trim()
    if (search.isNotEmpty()) {
      closeSearch()
      presenter.search(search)
      supportActionBar?.title = search
      supportActionBar?.setSubtitle(R.string.library_search_subtitle)
      searchMenuItem?.isVisible = false
      searchClear?.isVisible = true
    } else {
      presenter.search("")
    }

    return true
  }

  private fun closeSearch(): Boolean {
    searchView?.let {
      if (it.isShown) {
        it.isIconified = true
        it.isFocusable = false
        it.clearFocus()
        searchMenuItem?.collapseActionView()
        return true
      }
    }
    return false
  }

  override fun onQueryTextChange(newText: String): Boolean = false

  public override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(LIBRARY_SCOPE).installModules(LibraryModule())
    scope = Toothpick.openScopes(application, LIBRARY_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = ActivityLibraryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    super.setup()
    val tabs: TabLayout = binding.pagerTabStrip
    val pager = binding.searchPager
    pagerAdapter = LibraryPagerAdapter(this)
    pager.apply {
      adapter = pagerAdapter
    }

    TabLayoutMediator(tabs, pager) { currentTab, currentPosition ->
      currentTab.text = when (currentPosition) {
        Search.SECTION_ALBUM -> getString(R.string.label_albums)
        Search.SECTION_ARTIST -> getString(R.string.label_artists)
        Search.SECTION_GENRE -> getString(R.string.label_genres)
        Search.SECTION_TRACK -> getString(R.string.label_tracks)
        else -> throw IllegalArgumentException("invalid position")
      }
    }.attach()
    presenter.attach(this)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.library_search, menu)
    searchMenuItem = menu.findItem(R.id.library_search_item)
    searchClear = menu.findItem(R.id.library_search_clear)
    albumArtistOnly = menu.findItem(R.id.library_album_artist)
    searchView = searchMenuItem?.actionView as SearchView
    searchView!!.queryHint = getString(R.string.library_search_hint)
    searchView!!.setIconifiedByDefault(true)
    searchView!!.setOnQueryTextListener(this)
    presenter.loadArtistPreference()
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.library_refresh_item -> {
        presenter.refresh()
        return true
      }
      R.id.library_album_artist -> {
        albumArtistOnly?.let {
          it.isChecked = !it.isChecked
          presenter.setArtistPreference(it.isChecked)
        }

        return true
      }
      R.id.library_search_clear -> {
        supportActionBar?.setTitle(R.string.nav_library)
        supportActionBar?.subtitle = ""
        presenter.search("")
        searchMenuItem?.isVisible = true
        searchClear?.isVisible = false
        return true
      }
      R.id.library_sync_state -> {
        presenter.showStats()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun showStats(stats: LibraryStats) {
    val binding = LibraryStatsLayoutBinding.inflate(layoutInflater)
    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.library_stats__title)
      .setView(binding.root)
      .setPositiveButton(android.R.string.ok) { md, _ -> md.dismiss() }
      .show()

    binding.apply {
      libraryStatsGenreValue.text = "${stats.genres}"
      libraryStatsArtistValue.text = "${stats.artists}"
      libraryStatsAlbumValue.text = "${stats.albums}"
      libraryStatsTrackValue.text = "${stats.tracks}"
      libraryStatsPlaylistValue.text = "${stats.playlists}"
    }
  }

  override fun syncComplete(stats: LibraryStats) {
    val message = getString(
      R.string.library__sync_complete,
      stats.genres,
      stats.artists,
      stats.albums,
      stats.tracks,
      stats.playlists
    )
    Snackbar.make(binding.root, R.string.library__sync_complete, Snackbar.LENGTH_LONG)
      .setText(message)
      .show()
  }

  public override fun onDestroy() {
    presenter.detach()
    pagerAdapter = null
    Toothpick.closeScope(this)
    super.onDestroy()
    pagerAdapter = null

    if (isDestroyed) {
      Toothpick.closeScope(LIBRARY_SCOPE)
    }
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
    outState.putInt(PAGER_POSITION, binding.searchPager.currentItem)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    binding.searchPager.currentItem = savedInstanceState.getInt(PAGER_POSITION, 0)
  }

  override fun syncFailure() {
    Snackbar.make(binding.root, R.string.library__sync_failed, Snackbar.LENGTH_LONG).show()
  }

  override fun showSyncProgress() {
    binding.syncProgress.isGone = false
    binding.syncProgressText.isGone = false
  }

  override fun hideSyncProgress() {
    binding.syncProgress.isGone = true
    binding.syncProgressText.isGone = true
  }

  companion object {
    private const val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
    val LIBRARY_SCOPE: Class<*> = LibraryActivity::class.java
  }
}
