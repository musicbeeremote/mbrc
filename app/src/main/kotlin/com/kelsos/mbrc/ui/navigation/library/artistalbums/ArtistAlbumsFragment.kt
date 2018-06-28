package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksFragmentArgs
import kotterknife.bindView



class ArtistAlbumsFragment : Fragment(),
  ArtistAlbumsView,
  MenuItemSelectedListener<AlbumEntity> {

  private val recyclerView: RecyclerView by bindView(R.id.artist_albums__album_list)
  private val emptyView: Group by bindView(R.id.artist_albums__empty_view)


  lateinit var actionHandler: PopupActionHandler


  lateinit var adapter: AlbumEntryAdapter


  lateinit var presenter: ArtistAlbumsPresenter

  private lateinit var artist: String

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_artist_albums, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setMenuItemSelectedListener(this)

    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter

    presenter.attach(this)
    presenter.load(artist)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    artist = ArtistAlbumsFragmentArgs.fromBundle(checkNotNull(arguments)).artist
    val title = if (artist.isEmpty()) {
      getString(R.string.empty)
    } else {
      artist
    }
  }

  override fun onMenuItemSelected(action: String, item: AlbumEntity) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.albumSelected(action, item)
  }

  override fun onItemClicked(item: AlbumEntity) {
    val args = AlbumTracksFragmentArgs.Builder(item.album, item.artist).build()
    findNavController(this).navigate(R.id.album_tracks_fragment, args.toBundle())
  }

  override fun update(albums: PagedList<AlbumEntity>) {
    adapter.submitList(albums)
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}