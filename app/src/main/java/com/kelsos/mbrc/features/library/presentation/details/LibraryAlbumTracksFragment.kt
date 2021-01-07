package com.kelsos.mbrc.features.library.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.setAppBarTitle
import com.kelsos.mbrc.databinding.FragmentLibraryDetailsBinding
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.presentation.adapters.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.details.viemodels.AlbumTrackViewModel
import com.kelsos.mbrc.features.queue.Queue

class LibraryAlbumTracksFragment(
  private val trackAdapter: TrackAdapter,
  private val viewModel: AlbumTrackViewModel
) : Fragment(), MenuItemSelectedListener<Track> {
  private val args: LibraryAlbumTracksFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true)
    trackAdapter.setMenuItemSelectedListener(this)
    val binding: FragmentLibraryDetailsBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_library_details,
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
    viewModel.tracks.observe(viewLifecycleOwner) {
      trackAdapter.submitList(it)
    }
    viewModel.load(args.album, args.artist)
    setAppBarTitle(getString(R.string.library_album_tracks__title, args.album, args.artist))
    return binding.root
  }

  override fun onMenuItemSelected(action: Queue, item: Track) {
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
