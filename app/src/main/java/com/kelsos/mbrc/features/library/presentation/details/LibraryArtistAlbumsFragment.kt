package com.kelsos.mbrc.features.library.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.setAppBarTitle
import com.kelsos.mbrc.databinding.FragmentLibraryDetailsBinding
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.presentation.adapters.AlbumAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.ArtistAlbumViewModel
import com.kelsos.mbrc.features.queue.Queue

class LibraryArtistAlbumsFragment(
  private val viewModel: ArtistAlbumViewModel,
  private val albumAdapter: AlbumAdapter
) : Fragment(), MenuItemSelectedListener<Album> {
  private val args: LibraryArtistAlbumsFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    albumAdapter.setMenuItemSelectedListener(this)
    val binding: FragmentLibraryDetailsBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_library_details,
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
    viewModel.albums.observe(viewLifecycleOwner) {
      albumAdapter.submitList(it)
    }
    viewModel.load(args.artist)
    setAppBarTitle(args.artist)
    return binding.root
  }

  override fun onMenuItemSelected(action: Queue, item: Album) {
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
