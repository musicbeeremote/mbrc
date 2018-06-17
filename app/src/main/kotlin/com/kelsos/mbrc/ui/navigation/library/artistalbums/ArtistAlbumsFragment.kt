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
import toothpick.Toothpick
import javax.inject.Inject

class ArtistAlbumsFragment : Fragment(), ArtistAlbumsView, MenuItemSelectedListener<Album> {

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var adapter: AlbumEntryAdapter

  @Inject
  lateinit var presenter: ArtistAlbumsPresenter

  private lateinit var artist: String
  private var _binding: FragmentArtistAlbumsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_artist_albums, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(ArtistAlbumsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    artist = ArtistAlbumsFragmentArgs.fromBundle(requireArguments()).artist
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
    Snackbar.make(binding.root, R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
