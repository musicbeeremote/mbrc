package com.kelsos.mbrc.features.library.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.setAppBarTitle
import com.kelsos.mbrc.databinding.DialogLibraryStatsBinding
import com.kelsos.mbrc.databinding.FragmentLibraryBinding
import com.kelsos.mbrc.features.library.presentation.screens.AlbumScreen
import com.kelsos.mbrc.features.library.presentation.screens.ArtistScreen
import com.kelsos.mbrc.features.library.presentation.screens.GenreScreen
import com.kelsos.mbrc.features.library.presentation.screens.TrackScreen
import com.kelsos.mbrc.features.library.sync.SyncCategory
import org.koin.android.ext.android.inject

class LibraryFragment(
  private val viewModel: LibraryViewModel
) : Fragment(), OnQueryTextListener, CategoryRetriever {
  private lateinit var pagerAdapter: LibraryPagerAdapter
  private lateinit var searchView: SearchView
  private lateinit var searchMenuItem: MenuItem
  private lateinit var clearMenuItem: MenuItem

  override fun onQueryTextSubmit(query: String): Boolean {
    val search = query.trim()
    if (search.isNotEmpty()) {
      closeSearch()
      viewModel.search(search)
      setAppBarTitle(if (search.isBlank()) getString(R.string.nav_library) else search)
      searchMenuItem.isVisible = false
      clearMenuItem.isVisible = true
      return true
    }

    return false
  }

  private fun closeSearch(): Boolean {
    searchView.apply {
      if (isShown) {
        isIconified = true
        isFocusable = false
        clearFocus()
        searchMenuItem.collapseActionView()
        return@closeSearch true
      }
    }
    return false
  }

  override fun getCategory(category: Int): String = when (category) {
    SyncCategory.GENRES -> getString(R.string.library__category_genres)
    SyncCategory.ARTISTS -> getString(R.string.library__category_artists)
    SyncCategory.ALBUMS -> getString(R.string.library__category_albums)
    SyncCategory.TRACKS -> getString(R.string.library__category_tracks)
    SyncCategory.PLAYLISTS -> getString(R.string.library__category_playlists)
    else -> ""
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return false
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    val binding: FragmentLibraryBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_library,
      container,
      false
    )
    binding.lifecycleOwner = viewLifecycleOwner
    binding.sync = viewModel.syncProgress
    binding.category = this

    val genreScreen: GenreScreen by inject()
    val artistScreen: ArtistScreen by inject()
    val albumScreen: AlbumScreen by inject()
    val trackScreen: TrackScreen by inject()

    val pager = binding.libraryContainerPager
    val tabs = binding.libraryContainerTabs
    val view = binding.root
    val pagerAdapter = LibraryPagerAdapter(
      viewLifecycleOwner
    ).also {
      this.pagerAdapter = it
      pager.adapter = it
      pager.isUserInputEnabled = true
      val screens = listOf(
        genreScreen,
        artistScreen,
        albumScreen,
        trackScreen
      )
      it.submit(screens)
    }

    pager.adapter = pagerAdapter

    TabLayoutMediator(tabs, pager) { tab, position ->
      val resId = when (position) {
        0 -> R.string.label_genres
        1 -> R.string.label_artists
        2 -> R.string.label_albums
        3 -> R.string.label_tracks
        else -> error("invalid position")
      }

      tab.setText(resId)
    }.attach()

    genreScreen.setOnGenrePressedListener {
      val action = LibraryFragmentDirections.actionShowGenreArtists(it.genre)
      view.findNavController().navigate(action)
    }
    artistScreen.setOnArtistPressedListener {
      val action = LibraryFragmentDirections.actionShowArtistAlbums(it.artist)
      view.findNavController().navigate(action)
    }
    albumScreen.setOnAlbumPressedListener {
      val action = LibraryFragmentDirections.actionShowAlbumTracks(it.album, it.artist)
      view.findNavController().navigate(action)
    }

    return view
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.library_search, menu)
    clearMenuItem = menu.findItem(R.id.library__action_clear)
    searchMenuItem = menu.findItem(R.id.library__action_search).apply {
      searchView = actionView as SearchView
    }

    searchView.apply {
      queryHint = getString(R.string.library_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@LibraryFragment)
    }

    menu.findItem(R.id.library__action_only_album_artists).isChecked = viewModel.albumArtistOnly
    menu.findItem(R.id.library__action_show_sync_state).setOnMenuItemClickListener {
      showStats()
      return@setOnMenuItemClickListener true
    }
  }

  private fun showStats() {
    viewModel.updateStats()

    val binding: DialogLibraryStatsBinding = DataBindingUtil.inflate(
      layoutInflater,
      R.layout.dialog__library_stats,
      null,
      false
    )

    binding.viewModel = viewModel

    MaterialAlertDialogBuilder(requireActivity())
      .setTitle(R.string.library_stats__title)
      .setView(binding.root)
      .setPositiveButton(android.R.string.ok) { md, _ -> md.dismiss() }
      .show()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.library__action_refresh -> {
        viewModel.refresh()
        return true
      }
      R.id.library__action_clear -> {
        viewModel.search()
        searchMenuItem.isVisible = true
        clearMenuItem.isVisible = false
        setAppBarTitle(getString(R.string.nav_library))
        return true
      }
      R.id.library__action_only_album_artists -> {
        item.isChecked = !item.isChecked
        viewModel.setAlbumArtistOnly(item.isChecked)
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
