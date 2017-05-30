package com.kelsos.mbrc.ui.navigation.library.genre_artists

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.adapters.ArtistEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.extensions.enableHome
import com.kelsos.mbrc.extensions.initLinear
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.library.artists.Artist
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class GenreArtistsActivity : FontActivity(),
    GenreArtistsView,
    MenuItemSelectedListener {

  @BindView(R.id.genre_artists_recycler) lateinit var recyclerView: EmptyRecyclerView
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout

  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: GenreArtistsPresenter

  private var genre: String? = null
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_genre_artists)
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this),
        GenreArtistsModule())
    Toothpick.inject(this, scope)

    ButterKnife.bind(this)

    genre = intent?.extras?.getString(GENRE_NAME)

    if (genre == null) {
      finish()
      return
    }

    setSupportActionBar(toolbar)
    supportActionBar?.enableHome(genre)
    if (genre.isNullOrEmpty()) {
      supportActionBar?.setTitle(R.string.empty)
    }
    adapter.setMenuItemSelectedListener(this)
    recyclerView.initLinear(adapter, emptyView)
    presenter.attach(this)
    presenter.load(genre!!)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    actionHandler.artistSelected(menuItem, entry, this)
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler.artistSelected(artist, this)
  }

  override fun update(data: FlowCursorList<Artist>) {
   adapter.update(data)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onBackPressed() {
    finish()
  }

  companion object {
    const val GENRE_NAME = "genre_name"
  }
}

