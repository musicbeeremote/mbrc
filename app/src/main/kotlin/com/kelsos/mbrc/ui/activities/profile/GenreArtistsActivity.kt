package com.kelsos.mbrc.ui.activities.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class GenreArtistsActivity : FontActivity(), ArtistEntryAdapter.MenuItemSelectedListener {

  @BindView(R.id.genre_artists_recycler) lateinit var recyclerView: EmptyRecyclerView
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout

  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler

  private var genre: String? = null
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_genre_artists)
    ButterKnife.bind(this)

    val extras = intent.extras

    if (extras != null) {
      genre = extras.getString(GENRE_NAME)
    }

    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setDisplayShowHomeEnabled(true)
      actionBar.title = genre
    }

    adapter!!.init(genre)
    adapter!!.setMenuItemSelectedListener(this)
    recyclerView!!.layoutManager = LinearLayoutManager(this)
    recyclerView!!.adapter = adapter
    recyclerView!!.setEmptyView(emptyView)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    actionHandler!!.artistSelected(menuItem, entry, this)
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler!!.artistSelected(artist, this)
  }

  companion object {

    val GENRE_NAME = "genre_name"
  }
}// Required empty public constructor
