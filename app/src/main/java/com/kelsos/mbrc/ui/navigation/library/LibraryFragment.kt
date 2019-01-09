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
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentLibraryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment(), OnQueryTextListener {

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private var albumArtistOnly: MenuItem? = null
  private var searchClear: MenuItem? = null
  private var pagerAdapter: LibraryPagerAdapter? = null

  private val viewModel: LibraryViewModel by viewModel()

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
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.library_search, menu)
    searchMenuItem = menu.findItem(R.id.library_screen__action_search)?.apply {
      searchView = actionView as SearchView
    }

    albumArtistOnly = menu.findItem(R.id.library_album_artist)

    searchView?.apply {
      queryHint = getString(R.string.library_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@LibraryFragment)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.library_screen__action_refresh -> {
        viewModel.refresh()
        return true
      }
      R.id.library_album_artist -> {
        albumArtistOnly?.let {
          it.isChecked = !it.isChecked
          viewModel.setArtistPreference(it.isChecked)
        }

        return true
      }
      R.id.library_search_clear -> {
        searchMenuItem?.isVisible = true
        searchClear?.isVisible = false
        return true
      }
      R.id.library_sync_state -> {
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    pagerAdapter = null
    super.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(PAGER_POSITION, binding.searchPager.currentItem)
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    binding.searchPager.currentItem = savedInstanceState?.getInt(PAGER_POSITION, 0) ?: 0
  }

  companion object {
    private const val PAGER_POSITION = "com.kelsos.mbrc.ui.activities.nav.PAGER_POSITION"
  }
}
