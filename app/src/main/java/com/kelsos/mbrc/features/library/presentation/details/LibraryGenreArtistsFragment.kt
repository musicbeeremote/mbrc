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
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.ArtistAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.GenreArtistViewModel
import com.kelsos.mbrc.features.queue.Queue

class LibraryGenreArtistsFragment(
  private val artistAdapter: ArtistAdapter,
  private val viewModel: GenreArtistViewModel
) : Fragment(), MenuItemSelectedListener<Artist> {
  private val args: LibraryGenreArtistsFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    artistAdapter.setMenuItemSelectedListener(this)
    val binding: FragmentLibraryDetailsBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_library_details,
      container,
      false
    )
    binding.libraryDetailsList.apply {
      adapter = artistAdapter
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(
        requireContext(),
        LinearLayoutManager.VERTICAL,
        false
      )
    }
    viewModel.artists.observe(viewLifecycleOwner) {
      artistAdapter.submitList(it)
    }
    viewModel.load(args.genre)
    setAppBarTitle(args.genre)
    return binding.root
  }

  override fun onMenuItemSelected(action: Queue, item: Artist) {
    if (action === Queue.Default) {
      onItemClicked(item)
    } else {
      viewModel.queue(action, item)
    }
  }

  override fun onItemClicked(item: Artist) {
    val action = LibraryGenreArtistsFragmentDirections.actionShowArtistAlbums(item.artist)
    findNavController().navigate(action)
  }
}
