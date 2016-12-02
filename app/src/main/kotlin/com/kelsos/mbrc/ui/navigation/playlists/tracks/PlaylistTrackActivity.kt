package com.kelsos.mbrc.ui.navigation.playlists.tracks

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistTrackAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.PlaylistTrack
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieSupportActivityModule
import javax.inject.Inject

class PlaylistTrackActivity : AppCompatActivity(), PlaylistTrackView, PlaylistTrackAdapter.MenuItemSelectedListener {

  @BindView(R.id.playlist_recycler) internal lateinit var playlist: RecyclerView
  @BindView(R.id.toolbar) internal lateinit var toolbar: Toolbar

  @Inject lateinit var trackAdapter: PlaylistTrackAdapter
  @Inject lateinit var presenter: PlaylistTrackPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_playlist_tracks)
    ButterKnife.bind(this)
    val scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieSupportActivityModule(this), PlaylistTrackModule())
    Toothpick.inject(this, scope)

    presenter.bind(this)
    setSupportActionBar(toolbar)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = intent.getStringExtra(NAME)

    playlist.layoutManager = LinearLayoutManager(this)
    playlist.adapter = trackAdapter
    trackAdapter.setMenuItemSelectedListener(this)
    presenter.load(intent.getLongExtra(ID, 0))
  }

  public override fun onStart() {
    super.onStart()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun showErrorWhileLoading() {

  }

  override fun update(data: List<PlaylistTrack>) {
    trackAdapter.update(data)
  }

  override fun onMenuItemSelected(item: MenuItem, track: PlaylistTrack) {
    when (item.itemId) {
      R.id.popup_track_play -> presenter.queue(track, Queue.NOW)
      R.id.popup_track_playlist -> {
      }
      R.id.popup_track_queue_next -> presenter.queue(track, Queue.NEXT)
      R.id.popup_track_queue_last -> presenter.queue(track, Queue.LAST)
      else -> {
      }
    }
  }

  override fun onItemClicked(track: PlaylistTrack) {
    presenter.queue(track, Queue.NOW)
  }

  companion object {

    const val NAME = "name"
    const val ID = "path"
  }
}
