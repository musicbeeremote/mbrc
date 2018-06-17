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
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentLibraryBinding
import com.kelsos.mbrc.databinding.LibraryStatsLayoutBinding
import com.kelsos.mbrc.metrics.SyncedData
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class LibraryFragment :
  Fragment(),
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
  private var _binding: FragmentLibraryBinding? = null
  private val binding get() = _binding!!

  override fun onQueryTextSubmit(query: String): Boolean {
    val search = query.trim()
    if (search.isNotEmpty()) {
      closeSearch()
      searchMenuItem?.isVisible = false
      searchClear?.isVisible = true
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
  ): View {
    setHasOptionsMenu(true)
    _binding = FragmentLibraryBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    pagerAdapter = LibraryPagerAdapter(requireActivity())
    binding.searchPager.adapter = pagerAdapter
    binding.searchPager.offscreenPageLimit = 4

    TabLayoutMediator(
      binding.pagerTabStrip,
      binding.searchPager
    ) { currentTab, currentPosition ->
      currentTab.text = when (currentPosition) {
        Category.SECTION_ALBUM -> getString(R.string.label_albums)
        Category.SECTION_ARTIST -> getString(R.string.label_artists)
        Category.SECTION_GENRE -> getString(R.string.label_genres)
        Category.SECTION_TRACK -> getString(R.string.label_tracks)
        else -> throw IllegalArgumentException("invalid position")
      }
    }.attach()
    presenter.attach(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.library_search, menu)
    searchMenuItem = menu.findItem(R.id.library_search_item)?.apply {
      searchView = actionView as SearchView
    }

    albumArtistOnly = menu.findItem(R.id.library_album_artist)

    searchView?.apply {
      queryHint = getString(R.string.library_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@LibraryFragment)
    }

    presenter.loadArtistPreference()
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

  override fun showStats(stats: SyncedData) {
    val binding = LibraryStatsLayoutBinding.inflate(layoutInflater)
    MaterialAlertDialogBuilder(requireContext())
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

  override fun syncComplete(stats: SyncedData) {
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

  override fun onDestroy() {
    presenter.detach()
    pagerAdapter = null
    Toothpick.closeScope(this)
    Toothpick.closeScope(PRESENTER_SCOPE)

    super.onDestroy()
  }

  override fun updateArtistOnlyPreference(albumArtistOnly: Boolean?) {
    this.albumArtistOnly?.isChecked = albumArtistOnly ?: false
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(PAGER_POSITION, binding.searchPager.currentItem)
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    binding.searchPager.currentItem = savedInstanceState?.getInt(PAGER_POSITION, 0) ?: 0
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

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private const val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
