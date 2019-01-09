package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.kelsos.mbrc.databinding.FragmentArtistAlbumsBinding
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistAlbumsFragment : Fragment(), MenuItemSelectedListener<Album> {

  private val actionHandler: PopupActionHandler by inject()
  private val adapter: AlbumEntryAdapter by inject()
  private val viewModel: ArtistAlbumsViewModel by viewModel()

  private lateinit var artist: String
  private var _binding: FragmentArtistAlbumsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentArtistAlbumsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val args = ArtistAlbumsFragmentArgs.fromBundle(requireArguments())
    artist = args.artist
    adapter.setMenuItemSelectedListener(this)
    binding.artistAlbumsAlbumList.layoutManager = LinearLayoutManager(requireContext())
    binding.artistAlbumsAlbumList.adapter = adapter
    viewModel.albums.onEach {
      adapter.submitData(it)
      binding.artistAlbumsEmptyView.isGone = adapter.itemCount != 0
    }.launchIn(lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onMenuItemSelected(itemId: Int, item: Album) {
    val action = actionHandler.albumSelected(itemId)
    if (action == LibraryPopup.PROFILE) {
      onItemClicked(item)
    }
  }

  override fun onItemClicked(item: Album) {
    val directions = ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumTracksFragment(
      album = item.album,
      artist = item.artist
    )
    findNavController().navigate(directions)
  }

  suspend fun update(albums: PagingData<Album>) {
    adapter.submitData(albums)
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
