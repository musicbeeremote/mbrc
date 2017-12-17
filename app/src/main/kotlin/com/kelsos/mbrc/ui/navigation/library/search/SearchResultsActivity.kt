package com.kelsos.mbrc.ui.navigation.library.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultAdapter.OnSearchItemSelected
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class SearchResultsActivity : BaseActivity(),
                              SearchResultsView,
                              OnSearchItemSelected {

  private val searchResultsRecycler: EmptyRecyclerView by bindView(R.id.search_results_recycler)
  private val emptyViewText: TextView by bindView(R.id.empty_view_text)
  private val emptyView: LinearLayout by bindView(R.id.empty_view)

  @Inject lateinit var adapter: SearchResultAdapter
  @Inject lateinit var presenter: SearchResultsPresenter
  @Inject lateinit var actionHandler: PopupActionHandler

  private lateinit var scope: Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), SearchResultsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_search_results)


    val query = intent.getStringExtra(QUERY)

    presenter.attach(this)

    if (TextUtils.isEmpty(query)) {
      finish()
    } else {
      presenter.search(query)
    }

    setupToolbar(query)

    searchResultsRecycler.adapter = adapter
    searchResultsRecycler.emptyView = emptyView
    searchResultsRecycler.layoutManager = LinearLayoutManager(this)
    adapter.setOnSearchItemSelectedListener(this)
    emptyViewText.setText(R.string.no_results_found)

  }

  override fun update(searchResults: SearchResults) {
    adapter.update(searchResults)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun albumSelected(item: MenuItem, album: AlbumEntity) {
    actionHandler.albumSelected(item, album, this)
  }

  override fun albumSelected(album: AlbumEntity) {
    actionHandler.albumSelected(album, this)
  }

  override fun artistSelected(item: MenuItem, artist: ArtistEntity) {
    actionHandler.artistSelected(item, artist, this)
  }

  override fun artistSelected(artist: ArtistEntity) {
    actionHandler.artistSelected(artist, this)
  }

  override fun genreSelected(item: MenuItem, genre: GenreEntity) {
    actionHandler.genreSelected(item, genre, this)
  }

  override fun genreSelected(genre: GenreEntity) {
    actionHandler.genreSelected(genre, this)
  }

  override fun trackSelected(item: MenuItem, track: TrackEntity) {
    actionHandler.trackSelected(item, track)
  }

  override fun trackSelected(track: TrackEntity) {
    actionHandler.trackSelected(track)
  }

  companion object {
    val QUERY = "com.kelsos.mbrc.extras.QUERY"
  }
}
