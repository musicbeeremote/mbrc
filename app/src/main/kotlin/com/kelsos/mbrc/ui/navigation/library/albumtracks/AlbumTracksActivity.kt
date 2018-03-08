package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.databinding.ActivityAlbumTracksBinding
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import com.kelsos.mbrc.utilities.RemoteUtils.sha1
import com.squareup.picasso.Picasso
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.io.File
import javax.inject.Inject

class AlbumTracksActivity :
  BaseActivity(),
  AlbumTracksView,
  MenuItemSelectedListener<Track> {

  @Inject
  lateinit var adapter: TrackEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: AlbumTracksPresenter

  private var album: AlbumInfo? = null
  private lateinit var scope: Scope
  private lateinit var binding: ActivityAlbumTracksBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), AlbumTracksModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = ActivityAlbumTracksBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val extras = intent.extras

    if (extras != null) {
      album = extras.getParcelable(ALBUM)
    }

    val selectedAlbum = album
    if (selectedAlbum == null) {
      finish()
      return
    }

    val albumTitle = album?.album ?: ""
    val title = if (albumTitle.isBlank()) {
      getString(R.string.non_album_tracks)
    } else {
      albumTitle
    }

    setupToolbar(title, subtitle = selectedAlbum.artist)
    binding.albumTracksAlbum.text = selectedAlbum.album
    binding.albumTracksArtist.text = selectedAlbum.artist
    loadCover(selectedAlbum.artist, selectedAlbum.album)

    adapter.setMenuItemSelectedListener(this)
    val listTracks = binding.albumTracksTrackList
    listTracks.layoutManager = LinearLayoutManager(baseContext)
    listTracks.adapter = adapter

    val play = binding.albumTracksPlay
    play.isVisible = true
    play.setOnClickListener {
      presenter.queueAlbum(selectedAlbum.artist, selectedAlbum.album)
    }

    presenter.attach(this)
    presenter.load(album!!)
  }

  private fun loadCover(artist: String, album: String) {
    val image = binding.albumTracksCover
    val cache = File(cacheDir, "covers")
    Picasso.get()
      .load(File(cache, sha1("${artist}_$album")))
      .noFade()
      .config(Bitmap.Config.RGB_565)
      .error(R.drawable.ic_image_no_cover)
      .placeholder(R.drawable.ic_image_no_cover)
      .resizeDimen(R.dimen.cover_size, R.dimen.cover_size)
      .centerCrop()
      .into(image)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Track) {
    presenter.queue(item, actionHandler.trackSelected(itemId))
  }

  override fun onItemClicked(item: Track) {
    presenter.queue(item)
  }

  override suspend fun update(tracks: PagingData<Track>) {
    adapter.submitData(tracks)
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

  override fun onBackPressed() {
    finish()
  }

  companion object {
    const val ALBUM = "albumName"
  }
}
