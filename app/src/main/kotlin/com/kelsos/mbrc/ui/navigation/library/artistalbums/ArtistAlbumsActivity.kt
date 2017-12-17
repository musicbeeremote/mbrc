package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.LinearLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ArtistAlbumsActivity : BaseActivity(),
    ArtistAlbumsView,
    AlbumEntryAdapter.MenuItemSelectedListener {

  private val recyclerView: EmptyRecyclerView by bindView(R.id.album_recycler)
  private val emptyView: LinearLayout by bindView(R.id.empty_view)

  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var presenter: ArtistAlbumsPresenter

  private var artist: String? = null
  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this),
        ArtistAlbumsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_artist_albums)

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
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter
    recyclerView.emptyView = emptyView
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

  override fun onMenuItemSelected(menuItem: MenuItem, entry: AlbumEntity) {
    actionHandler.albumSelected(menuItem, entry, this)
  }

  override fun onItemClicked(album: AlbumEntity) {
    actionHandler.albumSelected(album, this)
  }

  override fun update(albums: List<AlbumEntity>) {
    adapter.update(albums)
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
    val ARTIST_NAME = "artist_name"
  }
}


