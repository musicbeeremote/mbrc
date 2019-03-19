package com.kelsos.mbrc.ui.navigation.library.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryFragmentDirections
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumFragment : Fragment(), MenuItemSelectedListener<Album> {

  private val adapter: AlbumAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: AlbumViewModel by viewModel()

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!

  fun search(term: String) {
    binding.libraryBrowserSync.isGone = term.isNotEmpty()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.libraryBrowserTextTitle.setText(R.string.albums_list_empty)
    binding.libraryBrowserSync.setOnClickListener {
    }
    binding.libraryBrowserContent.adapter = adapter
    binding.libraryBrowserContent.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryBrowserContent.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    viewModel.albums.onEach {
      adapter.submitData(it)
      binding.libraryBrowserEmptyGroup.isGone = adapter.itemCount != 0
    }.launchIn(lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Album) {
    val action = actionHandler.albumSelected(itemId)
    if (action == LibraryPopup.PROFILE) {
      onItemClicked(item)
    }
  }

  override fun onItemClicked(item: Album) {
    val directions = LibraryFragmentDirections.actionLibraryFragmentToAlbumTracksFragment(
      artist = item.artist,
      album = item.album
    )
    findNavController().navigate(directions)
  }

  suspend fun update(albums: PagingData<Album>) {
    adapter.submitData(albums)
    binding.libraryBrowserEmptyGroup.isGone = adapter.itemCount != 0
  }

  fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(requireView(), R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }
}
