package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import kotterknife.bindView
import org.koin.android.ext.android.inject


class AlbumTracksFragment : Fragment(), MenuItemSelectedListener<TrackEntity> {

  private val listTracks: RecyclerView by bindView(R.id.album_tracks__track_list)
  private val emptyView: Group by bindView(R.id.album_tracks__empty_view)
  private val playAlbum: FloatingActionButton by bindView(R.id.play_album)
  
  private val adapter: TrackEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: AlbumTracksViewModel by inject()

  private lateinit var album: AlbumInfo

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_album_tracks, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setMenuItemSelectedListener(this)
    listTracks.layoutManager = LinearLayoutManager(requireContext())
    listTracks.adapter = adapter


    presenter.load(album)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    album = AlbumTracksFragmentArgs.fromBundle(checkNotNull(arguments)).run {
      AlbumInfo(album, artist)
    }

    val albumTitle = album.album
    val title = if (albumTitle.isBlank()) {
      getString(R.string.non_album_tracks)
    } else {
      albumTitle
    }
  }

  override fun onMenuItemSelected(action: String, item: TrackEntity) {
    actionHandler.trackSelected(action, item, true)
  }

  override fun onItemClicked(item: TrackEntity) {
    actionHandler.trackSelected(item, true)
  }

  fun update(pagedList: PagedList<TrackEntity>) {
    adapter.submitList(pagedList)
  }
}