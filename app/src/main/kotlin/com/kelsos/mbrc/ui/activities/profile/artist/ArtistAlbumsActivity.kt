package com.kelsos.mbrc.ui.activities.profile.artist

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.AlbumEntryAdapter
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ArtistAlbumsActivity : FontActivity(), AlbumEntryAdapter.MenuItemSelectedListener {

  @BindView(R.id.album_recycler) lateinit var recyclerView: EmptyRecyclerView
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout

  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var adapter: AlbumEntryAdapter

  private var artist: String? = null
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_artist_albums)
    ButterKnife.bind(this)

    val extras = intent.extras
    if (extras != null) {
      artist = extras.getString(ARTIST_NAME)
    }

    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setDisplayShowHomeEnabled(true)
      actionBar.title = artist
    }

    adapter.setMenuItemSelectedListener(this)
    adapter.init(artist)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter
    recyclerView.emptyView = emptyView
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Album) {
    actionHandler.albumSelected(menuItem, entry, this)
  }

  override fun onItemClicked(album: Album) {
    actionHandler.albumSelected(album, this)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  companion object {

    val ARTIST_NAME = "artist_name"
  }
}// Required empty public constructor
