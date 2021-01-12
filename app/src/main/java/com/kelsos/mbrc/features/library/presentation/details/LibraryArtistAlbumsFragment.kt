package com.kelsos.mbrc.features.library.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.common.ui.extensions.setAppBarTitle
import com.kelsos.mbrc.databinding.FragmentLibraryDetailsBinding
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.presentation.AlbumAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.ArtistAlbumViewModel
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LibraryArtistAlbumsFragment(
  private val viewModel: ArtistAlbumViewModel,
  private val albumAdapter: AlbumAdapter,
  private val actionHandler: PopupActionHandler
) : Fragment(), MenuItemSelectedListener<Album> {
  private val args: LibraryArtistAlbumsFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    albumAdapter.setMenuItemSelectedListener(this)
    val binding = FragmentLibraryDetailsBinding.inflate(
      inflater,
      container,
      false
    )
    binding.libraryDetailsList.apply {
      adapter = albumAdapter
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(
        requireContext(),
        LinearLayoutManager.VERTICAL,
        false
      )
    }
    lifecycleScope.launch {
      viewModel.albums.collect {
        albumAdapter.submitData(it)
      }
    }
    viewModel.load(args.artist)
    setAppBarTitle(args.artist)
    return binding.root
  }

  override fun onMenuItemSelected(itemId: Int, item: Album) {
    val action = actionHandler.albumSelected(itemId)
    if (action == Queue.Default) {
      onItemClicked(item)
    } else {
      viewModel.queue(action, item)
    }
  }

  override fun onItemClicked(item: Album) {
    val actions = LibraryArtistAlbumsFragmentDirections.actionShowAlbumTracks(
      item.album,
      item.artist
    )
    findNavController().navigate(actions)
  }
}
