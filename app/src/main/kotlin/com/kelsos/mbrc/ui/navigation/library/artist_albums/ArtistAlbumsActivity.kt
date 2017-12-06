package com.kelsos.mbrc.ui.navigation.library.artist_albums

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ArtistAlbumsActivity : FontActivity(),
    ArtistAlbumsView,
    AlbumEntryAdapter.MenuItemSelectedListener {

  private val recyclerView: EmptyRecyclerView by bindView(R.id.album_recycler)
  private val toolbar: Toolbar by bindView(R.id.toolbar)
  private val emptyView: LinearLayout by bindView(R.id.empty_view)

  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var adapter: AlbumEntryAdapter
  @Inject lateinit var presenter: ArtistAlbumsPresenter

  private var artist: String? = null
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this),
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

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = artist

    if (artist.isNullOrEmpty()) {
      supportActionBar?.setTitle(R.string.empty)
    }

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

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Album) {
    actionHandler.albumSelected(menuItem, entry, this)
  }

  override fun onItemClicked(album: Album) {
    actionHandler.albumSelected(album, this)
  }

  override fun update(albums: FlowCursorList<Album>) {
    adapter.update(albums)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onBackPressed() {
    finish()
  }

  companion object {
    val ARTIST_NAME = "artist_name"
  }
}


