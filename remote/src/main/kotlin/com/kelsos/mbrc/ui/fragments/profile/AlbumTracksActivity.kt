package com.kelsos.mbrc.ui.fragments.profile

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumProfileAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.presenters.AlbumTracksPresenter
import com.kelsos.mbrc.ui.views.AlbumTrackView
import com.squareup.picasso.Picasso
import roboguice.RoboGuice
import java.io.File

class AlbumTracksActivity : AppCompatActivity(), AlbumTrackView, AlbumProfileAdapter.MenuItemSelectedListener {
  @Bind(R.id.imageView_list) internal lateinit var imageViewList: ImageView
  @Bind(R.id.toolbar) internal lateinit var toolbar: Toolbar
  @Bind(R.id.collapsing_toolbar) internal lateinit var collapsingToolbar: CollapsingToolbarLayout
  @Bind(R.id.app_bar_layout) internal lateinit var appBarLayout: AppBarLayout
  @Bind(R.id.list_tracks) internal lateinit var listTracks: RecyclerView
  @Bind(R.id.album_title) internal lateinit var albumTitle: TextView
  @Bind(R.id.album_year) internal lateinit var albumYear: TextView
  @Bind(R.id.album_tracks) internal lateinit var albumTracks: TextView

  @Inject private lateinit var adapter: AlbumProfileAdapter
  @Inject private lateinit var presenter: AlbumTracksPresenter
  private var albumId: Long = 0

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_album_tracks)
    RoboGuice.getInjector(this).injectMembers(this)
    ButterKnife.bind(this)
    presenter.bind(this)
    val extras = intent.extras

    if (extras != null) {
      albumId = extras.getLong(ALBUM_ID, 0)
    }

    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setDisplayShowHomeEnabled(true)
    }

    collapsingToolbar.isTitleEnabled = true
    listTracks.layoutManager = LinearLayoutManager(baseContext)
    listTracks.adapter = adapter

    if (albumId == 0L) {
      finish()
    }
    adapter.setListener(this)

    presenter.load(albumId)
  }

  override fun updateAlbum(album: Album) {
    val cover = album.cover

    albumTitle.text = album.name
    albumYear.text = album.year
    collapsingToolbar.title = album.artist

    if (!TextUtils.isEmpty(cover)) {

      val image = File(File(filesDir, "covers"), cover)

      Picasso.with(baseContext).load(image).placeholder(R.drawable.ic_image_no_cover).fit().centerCrop().into(
          imageViewList)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      finish()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun updateTracks(tracks: List<Track>) {
    adapter.updateData(tracks)
    albumTracks.text = getString(R.string.number_of_tracks, tracks.size)
  }

  override fun showPlaySuccess() {
    showSnackbar(R.string.album_play_success)
  }

  private fun showSnackbar(@StringRes resId: Int) {
    Snackbar.make(appBarLayout, resId, Snackbar.LENGTH_SHORT).show()
  }

  override fun showPlayFailed() {
    showSnackbar(R.string.album_play_failure)
  }

  override fun showTrackSuccess() {
    showSnackbar(R.string.track_added_successfully)
  }

  override fun showTrackFailed() {
    showSnackbar(R.string.track_add_failed)
  }

  @OnClick(R.id.play_album) fun onPlayClicked() {
    presenter.play(albumId)
  }


  override fun onMenuItemSelected(menuItem: MenuItem, entry: Track) {
    when (menuItem.itemId) {
      R.id.popup_track_play -> presenter.queue(entry, Queue.NOW)
      R.id.popup_track_queue_last -> presenter.queue(entry, Queue.LAST)
      R.id.popup_track_queue_next -> presenter.queue(entry, Queue.NEXT)
      R.id.popup_track_playlist -> {
      }
      else -> {
      }
    }
  }

  override fun onItemClicked(track: Track) {
    presenter.queue(track, Queue.NOW)
  }

  companion object {
    const val ALBUM_ID = "albumId"
  }
}
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
