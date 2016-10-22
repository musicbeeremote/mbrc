package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.presenters.PlaylistTrackPresenter
import com.kelsos.mbrc.ui.views.PlaylistTrackView
import roboguice.RoboGuice

class PlaylistTrackActivity : AppCompatActivity(), PlaylistTrackView, PlaylistAdapter.MenuItemSelectedListener {

  @BindView(R.id.playlist_recycler) internal lateinit var playlist: RecyclerView
  @BindView(R.id.toolbar) internal lateinit var toolbar: Toolbar

  @Inject private lateinit var adapter: PlaylistAdapter
  @Inject private lateinit var presenter: PlaylistTrackPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_playlist)
    ButterKnife.bind(this)
    RoboGuice.getInjector(this).injectMembers(this)
    presenter.bind(this)
    setSupportActionBar(toolbar)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = intent.getStringExtra(NAME)

    playlist.layoutManager = LinearLayoutManager(this)
    playlist.adapter = adapter
    adapter.setMenuItemSelectedListener(this)
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
    adapter.update(data)
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
