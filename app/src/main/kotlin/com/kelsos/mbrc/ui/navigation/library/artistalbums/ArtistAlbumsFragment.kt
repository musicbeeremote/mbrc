package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
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
import org.koin.android.ext.android.inject

class ArtistAlbumsFragment : Fragment(), ArtistAlbumsView, MenuItemSelectedListener<Album> {

  private val actionHandler: PopupActionHandler by inject()
  private val adapter: AlbumEntryAdapter by inject()
  private val presenter: ArtistAlbumsPresenter by inject()

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

    presenter.attach(this)
    presenter.load(artist)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onMenuItemSelected(itemId: Int, item: Album) {
    val action = actionHandler.albumSelected(itemId)
    if (action == LibraryPopup.PROFILE) {
      onItemClicked(item)
    } else {
      presenter.queue(action, item)
    }
  }

  override fun onItemClicked(item: Album) {
    val directions = ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumTracksFragment(
      album = item.album,
      artist = item.artist
    )
    findNavController(this).navigate(directions)
  }

  override suspend fun update(albums: PagingData<Album>) {
    adapter.submitData(albums)
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

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}
