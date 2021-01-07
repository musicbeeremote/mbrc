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
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.adapters.ArtistAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.GenreArtistViewModel
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LibraryGenreArtistsFragment(
  private val artistAdapter: ArtistAdapter,
  private val viewModel: GenreArtistViewModel,
  private val actionHandler: PopupActionHandler
) : Fragment(), MenuItemSelectedListener<Artist> {
  private val args: LibraryGenreArtistsFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    artistAdapter.setMenuItemSelectedListener(this)
    val binding = FragmentLibraryDetailsBinding.inflate(
      inflater,
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
    lifecycleScope.launch {
      viewModel.artists.collect {
        artistAdapter.submitData(it)
      }
    }
    viewModel.load(args.genre)
    setAppBarTitle(args.genre)
    return binding.root
  }

  override fun onMenuItemSelected(itemId: Int, item: Artist) {
    val action = actionHandler.artistSelected(itemId)
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
