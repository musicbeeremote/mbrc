package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class AlbumTracksActivity : BaseActivity(),
    AlbumTracksView,
    TrackEntryAdapter.MenuItemSelectedListener {

  private val listTracks: RecyclerView by bindView(R.id.album_tracks__track_list)
  private val emptyView: Group by bindView(R.id.album_tracks__empty_view)
  private val playAlbum: FloatingActionButton by bindView(R.id.play_album)

  @Inject lateinit var adapter: TrackEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: AlbumTracksPresenter

  private var album: AlbumInfo? = null
  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), AlbumTracksModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_album_tracks)

    val extras = intent.extras

    if (extras != null) {
      album = extras.getParcelable(ALBUM)
    }

    if (album == null) {
      finish()
      return
    }

    val albumTitle = album?.album ?: ""
    val title = if (albumTitle.isBlank()) {
      getString(R.string.non_album_tracks)
    } else {
      albumTitle
    }

    setupToolbar(title)

    adapter.setMenuItemSelectedListener(this)
    listTracks.layoutManager = LinearLayoutManager(baseContext)
    listTracks.adapter = adapter

    presenter.attach(this)
    presenter.load(album!!)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onMenuItemSelected(action: String, entry: TrackEntity) {
    actionHandler.trackSelected(action, entry, true)
  }

  override fun onItemClicked(track: TrackEntity) {
    actionHandler.trackSelected(track, true)
  }

  override fun update(pagedList: List<TrackEntity>) {
    adapter.setList(pagedList)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onBackPressed() {
    finish()
  }

  companion object {
    val ALBUM = "albumName"
  }
}
