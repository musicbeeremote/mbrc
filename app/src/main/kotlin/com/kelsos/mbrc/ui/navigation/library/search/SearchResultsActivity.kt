package com.kelsos.mbrc.ui.navigation.library.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultAdapter.OnSearchItemSelected
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class SearchResultsActivity : BaseActivity(),
  SearchResultsView,
  OnSearchItemSelected {

  private val searchResultsRecycler: RecyclerView by bindView(R.id.content_search__search_results)
  private val emptyView: Group by bindView(R.id.content_search__empty_group)

  @Inject
  lateinit var adapter: SearchResultAdapter
  @Inject
  lateinit var presenter: SearchResultsPresenter
  @Inject
  lateinit var actionHandler: PopupActionHandler

  private lateinit var scope: Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), SearchResultsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_search_results)

    val query = intent.getStringExtra(QUERY)

    presenter.attach(this)

    if (query.isNullOrBlank()) {
      finish()
    } else {
      presenter.search(query)
    }

    setupToolbar(query)

    searchResultsRecycler.adapter = adapter
    searchResultsRecycler.layoutManager = LinearLayoutManager(this)
    adapter.setOnSearchItemSelectedListener(this)
  }

  override fun update(searchResults: SearchResults) {
    emptyView.isVisible = searchResults.empty()
    adapter.update(searchResults)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean = when {
    item.itemId == android.R.id.home -> {
      finish()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun albumSelected(action: String, album: AlbumEntity) {
    actionHandler.albumSelected(action, album, this)
  }

  override fun albumSelected(album: AlbumEntity) {
    actionHandler.albumSelected(album, this)
  }

  override fun artistSelected(action: String, artist: ArtistEntity) {
    actionHandler.artistSelected(action, artist, this)
  }

  override fun artistSelected(artist: ArtistEntity) {
    actionHandler.artistSelected(artist, this)
  }

  override fun genreSelected(action: String, genre: GenreEntity) {
    actionHandler.genreSelected(action, genre, this)
  }

  override fun genreSelected(genre: GenreEntity) {
    actionHandler.genreSelected(genre, this)
  }

  override fun trackSelected(action: String, track: TrackEntity) {
    actionHandler.trackSelected(action, track)
  }

  override fun trackSelected(track: TrackEntity) {
    actionHandler.trackSelected(track)
  }

  companion object {
    val QUERY = "com.kelsos.mbrc.extras.QUERY"

    fun start(context: Context, queryString: String) {
      val searchIntent = Intent(context, SearchResultsActivity::class.java)
      searchIntent.putExtra(SearchResultsActivity.QUERY, queryString)
      context.startActivity(searchIntent)
    }
  }
}