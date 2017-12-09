package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.LinearLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class AlbumTracksActivity : BaseActivity(),
    AlbumTracksView,
    TrackEntryAdapter.MenuItemSelectedListener {

  private val listTracks: EmptyRecyclerView by bindView(R.id.list_tracks)
  private val emptyView: LinearLayout by bindView(R.id.empty_view)
  private val playAlbum: FloatingActionButton by bindView(R.id.play_album)

  @Inject lateinit var adapter: TrackEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: AlbumTracksPresenter

  private var album: AlbumInfo? = null
  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this),
        AlbumTracksModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_album_tracks)

    val extras = intent.extras

    if (extras != null) {
      album = extras.getParcelable<AlbumInfo>(ALBUM)
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
    listTracks.emptyView = emptyView

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

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Track) {
    actionHandler.trackSelected(menuItem, entry, true)
  }

  override fun onItemClicked(track: Track) {
    actionHandler.trackSelected(track, true)
  }

  override fun update(cursor: List<Track>) {
    adapter.update(cursor)
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
