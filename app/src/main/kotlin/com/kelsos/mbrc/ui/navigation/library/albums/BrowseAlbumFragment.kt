package com.kelsos.mbrc.ui.navigation.library.albums

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
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryActivity.Companion.LIBRARY_SCOPE
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class BrowseAlbumFragment :
  Fragment(),
  BrowseAlbumView,
  AlbumEntryAdapter.MenuItemSelectedListener {

  @Inject
  lateinit var adapter: AlbumEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: BrowseAlbumPresenter

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!

  override fun search(term: String) {
    binding.libraryBrowserSync.isGone = term.isNotEmpty()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, LIBRARY_SCOPE, activity, this)
    scope.installModules(SmoothieActivityModule(requireActivity()), BrowseAlbumModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    presenter.attach(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.libraryBrowserTextTitle.setText(R.string.albums_list_empty)
    binding.libraryBrowserSync.setOnClickListener {
      presenter.sync()
    }
    binding.libraryBrowserContent.adapter = adapter
    binding.libraryBrowserContent.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryBrowserContent.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, album: Album) {
    val action = actionHandler.albumSelected(menuItem, album, requireActivity())
    if (action != Queue.PROFILE) {
      presenter.queue(action, album)
    }
  }

  override fun onItemClicked(album: Album) {
    actionHandler.albumSelected(album, requireActivity())
  }

  override fun update(cursor: List<Album>) {
    adapter.update(cursor)
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

  override fun showLoading() {
    binding.libraryBrowserEmptyGroup.isGone = true
    binding.libraryBrowserLoadingBar.isGone = false
  }

  override fun hideLoading() {
    binding.libraryBrowserEmptyGroup.isGone = false
    binding.libraryBrowserLoadingBar.isGone = true
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
