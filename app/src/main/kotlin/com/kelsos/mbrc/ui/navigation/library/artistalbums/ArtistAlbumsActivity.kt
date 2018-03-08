package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.databinding.ActivityArtistAlbumsBinding
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ArtistAlbumsActivity :
  BaseActivity(),
  ArtistAlbumsView,
  MenuItemSelectedListener<Album> {

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var adapter: AlbumEntryAdapter

  @Inject
  lateinit var presenter: ArtistAlbumsPresenter

  private var artist: String? = null
  private lateinit var scope: Scope
  private lateinit var binding: ActivityArtistAlbumsBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), ArtistAlbumsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = ActivityArtistAlbumsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val extras = intent.extras
    if (extras != null) {
      artist = extras.getString(ARTIST_NAME)
    }

    if (artist == null) {
      finish()
      return
    }

    val title = artist ?: getString(R.string.empty)
    setupToolbar(title)

    adapter.setMenuItemSelectedListener(this)
    binding.artistAlbumsAlbumList.layoutManager = LinearLayoutManager(this)
    binding.artistAlbumsAlbumList.adapter = adapter
    presenter.attach(this)
    presenter.load(artist!!)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Album) {
    val action = actionHandler.albumSelected(itemId, item, this)
    if (action != LibraryPopup.PROFILE) {
      presenter.queue(action, item)
    }
  }

  override fun onItemClicked(item: Album) {
    actionHandler.albumSelected(item, this)
  }

  override suspend fun update(albums: PagingData<Album>) {
    adapter.submitData(albums)
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
    const val ARTIST_NAME = "artist_name"
  }
}
