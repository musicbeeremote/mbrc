package com.kelsos.mbrc.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.presenters.ArtistAlbumPresenter
import com.kelsos.mbrc.ui.views.ArtistAlbumsView
import roboguice.RoboGuice

class ArtistAlbumsActivity : AppCompatActivity(), ArtistAlbumsView, AlbumAdapter.MenuItemSelectedListener {
  @BindView(R.id.album_recycler) lateinit internal var recyclerView: RecyclerView
  @BindView(R.id.toolbar) lateinit internal var toolbar: Toolbar
  @Inject private lateinit var adapter: AlbumAdapter
  @Inject private lateinit var presenter: ArtistAlbumPresenter

  private var artistId: Long = 0

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_artist_albums)
    RoboGuice.getInjector(this).injectMembers(this)
    ButterKnife.bind(this)
    presenter.bind(this)
    val manager = GridLayoutManager(this, 2)
    recyclerView.layoutManager = manager
    recyclerView.adapter = adapter

    adapter.setMenuItemSelectedListener(this)
    var title = ""

    val extras = intent.extras
    if (extras != null) {
      artistId = extras.getLong(ARTIST_ID, 0)
      title = extras.getString(ARTIST_NAME, "")
    }

    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setDisplayShowHomeEnabled(true)
      actionBar.title = title
    }

    presenter.load(artistId)
  }

  override fun update(data: List<Album>) {
    adapter.updateData(data)
  }

  override fun showLoadFailed() {

  }

  override fun queueSuccess() {

  }

  override fun queueFailed() {

  }

  override fun onMenuItemSelected(menuItem: MenuItem, album: Album): Boolean {

    when (menuItem.itemId) {
      R.id.popup_album_play -> presenter.queue(Queue.NOW, album)
      R.id.popup_album_tracks -> openProfile(album)
      R.id.popup_album_queue_next -> presenter.queue(Queue.NEXT, album)
      R.id.popup_album_queue_last -> presenter.queue(Queue.LAST, album)
      R.id.popup_album_playlist -> {
      }
    }

    return true
  }

  override fun onItemClicked(album: Album) {
    openProfile(album)
  }

  private fun openProfile(album: Album) {
    val intent = Intent(this, AlbumTracksActivity::class.java)
    intent.putExtra(AlbumTracksActivity.ALBUM_ID, album.id)
    startActivity(intent)
  }

  companion object {

    const val ARTIST_ID = "artist_id"
    const val ARTIST_NAME = "artist_name"
  }
}// Required empty public constructor
