package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.databinding.ActivityArtistAlbumsBinding
import com.kelsos.mbrc.databinding.EmptyListBinding
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ArtistAlbumsActivity :
  BaseActivity(),
  ArtistAlbumsView,
  AlbumEntryAdapter.MenuItemSelectedListener {

  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var presenter: ArtistAlbumsPresenter

  private var artist: String? = null
  private lateinit var scope: Scope
  private lateinit var binding: ActivityArtistAlbumsBinding
  private lateinit var emptyBinding: EmptyListBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(
      SmoothieActivityModule(this),
      ArtistAlbumsModule()
    )
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = ActivityArtistAlbumsBinding.inflate(layoutInflater)
    emptyBinding = EmptyListBinding.bind(binding.root)
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
    binding.albumRecycler.layoutManager = LinearLayoutManager(this)
    binding.albumRecycler.adapter = adapter
    binding.albumRecycler.emptyView = emptyBinding.emptyView
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

  override fun onMenuItemSelected(menuItem: MenuItem, album: Album) {
    val action = actionHandler.albumSelected(menuItem, album, this)
    if (action != Queue.PROFILE) {
      presenter.queue(action, album)
    }
  }

  override fun onItemClicked(album: Album) {
    actionHandler.albumSelected(album, this)
  }

  override fun update(albums: FlowCursorList<Album>) {
    adapter.update(albums)
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
