package com.kelsos.mbrc.features.library.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.setAppBarTitle
import com.kelsos.mbrc.databinding.FragmentLibraryDetailsBinding
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.presentation.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.AlbumTrackViewModel
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LibraryAlbumTracksFragment(
  private val trackAdapter: TrackAdapter,
  private val viewModel: AlbumTrackViewModel,
  private val actionHandler: PopupActionHandler
) : Fragment(), MenuItemSelectedListener<Track> {
  private val args: LibraryAlbumTracksFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    trackAdapter.setMenuItemSelectedListener(this)
    val binding = FragmentLibraryDetailsBinding.inflate(
      inflater,
      container,
      false
    )
    binding.libraryDetailsList.apply {
      adapter = trackAdapter
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(
        requireContext(),
        LinearLayoutManager.VERTICAL,
        false
      )
    }
    lifecycleScope.launch {
      viewModel.tracks.collect {
        trackAdapter.submitData(it)
      }
    }
    viewModel.load(args.album, args.artist)
    setAppBarTitle(getString(R.string.library_album_tracks__title, args.album, args.artist))
    return binding.root
  }

  override fun onMenuItemSelected(itemId: Int, item: Track) {
    val action = actionHandler.trackSelected(itemId)
    if (action == Queue.Default) {
      onItemClicked(item)
    } else {
      viewModel.queue(action, item)
    }
  }

  override fun onItemClicked(item: Track) {
    viewModel.queue(Queue.Default, item)
  }
}
