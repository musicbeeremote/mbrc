package com.kelsos.mbrc.ui.navigation.library.album_tracks

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.TrackEntryAdapter
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class AlbumTracksActivity : FontActivity(),
  AlbumTracksView,
  TrackEntryAdapter.MenuItemSelectedListener {

  @Inject
  lateinit var adapter: TrackEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: AlbumTracksPresenter

  private var album: AlbumInfo? = null
  private var scope: Scope? = null
  private lateinit var recyclerView: EmptyRecyclerView

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(
      SmoothieActivityModule(this),
      AlbumTracksModule()
    )
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_album_tracks)
    val extras = intent.extras

    if (extras != null) {
      album = extras.getParcelable(ALBUM)
    }

    val selectedAlbum = album
    if (selectedAlbum == null) {
      finish()
      return
    }

    setSupportActionBar(findViewById(R.id.toolbar))
    val supportActionBar = supportActionBar ?: error("Actionbar should not be null")
    supportActionBar.setDisplayHomeAsUpEnabled(true)
    supportActionBar.setDisplayShowHomeEnabled(true)

    if (selectedAlbum.album.isEmpty()) {
      supportActionBar.setTitle(R.string.non_album_tracks)
    } else {
      supportActionBar.title = selectedAlbum.album
    }
    supportActionBar.subtitle = selectedAlbum.artist

    presenter.attach(this)
    presenter.load(selectedAlbum)
    adapter.setMenuItemSelectedListener(this)
    recyclerView = findViewById(R.id.list_tracks)
    recyclerView.layoutManager = LinearLayoutManager(baseContext)
    recyclerView.adapter = adapter
    recyclerView.emptyView = findViewById(R.id.empty_view)
    val fab = findViewById<FloatingActionButton>(R.id.play_album)
    fab.isVisible = true
    fab.setOnClickListener {
      presenter.queueAlbum(selectedAlbum.artist, selectedAlbum.album)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, track: Track) {
    presenter.queue(track, actionHandler.trackSelected(menuItem))
  }

  override fun onItemClicked(track: Track) {
    presenter.queue(track)
  }

  override fun update(cursor: FlowCursorList<Track>) {
    adapter.update(cursor)
  }

  override fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(recyclerView, R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onBackPressed() {
    finish()
  }

  companion object {
    const val ALBUM = "albumName"
  }
}
