package com.kelsos.mbrc.ui.navigation.library.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryActivity.Companion.LIBRARY_SCOPE
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter.MenuItemSelectedListener
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment :
  Fragment(),
  BrowseArtistView,
  MenuItemSelectedListener {

  @Inject
  lateinit var adapter: ArtistEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: BrowseArtistPresenter

  private var scope: Scope? = null

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!

  override fun search(term: String) {
    binding.libraryBrowserEmptyGroup.isGone = term.isNotEmpty()
  }

  override fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(requireView(), R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope =
      Toothpick.openScopes(requireActivity().application, LIBRARY_SCOPE, requireActivity(), this)
    scope?.installModules(BrowseArtistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.libraryBrowserTextTitle.setText(R.string.artists_list_empty)
    binding.libraryBrowserSync.setOnClickListener {
      presenter.sync()
    }
    binding.libraryBrowserContent.setHasFixedSize(true)
    binding.libraryBrowserContent.adapter = adapter
    binding.libraryBrowserContent.layoutManager = LinearLayoutManager(requireContext())
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, artist: Artist) {
    val action = actionHandler.artistSelected(menuItem, artist, requireActivity())
    if (action != Queue.PROFILE) {
      presenter.queue(action, artist)
    }
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler.artistSelected(artist, requireActivity())
  }

  override fun update(data: List<Artist>) {
    adapter.update(data)
  }

  override fun showLoading() {
    binding.libraryBrowserEmptyGroup.isGone = true
    binding.libraryBrowserLoadingBar.isGone = false
  }

  override fun hideLoading() {
    binding.libraryBrowserEmptyGroup.isGone = false
    binding.libraryBrowserLoadingBar.isGone = true
  }
}
