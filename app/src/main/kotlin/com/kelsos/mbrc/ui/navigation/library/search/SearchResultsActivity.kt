package com.kelsos.mbrc.ui.navigation.library.search

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.navigation.library.search.SearchResultAdapter.OnSearchItemSelected
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class SearchResultsActivity : FontActivity(),
                              SearchResultsView,
                              OnSearchItemSelected {
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.search_results_recycler) lateinit var searchResultsRecycler: EmptyRecyclerView
  @BindView(R.id.empty_view_text) lateinit var emptyViewText: TextView
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout

  @Inject lateinit var adapter: SearchResultAdapter
  @Inject lateinit var presenter: SearchResultsPresenter
  @Inject lateinit var actionHandler: PopupActionHandler
  private var scope: Scope? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this), SearchResultsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_search_results)
    ButterKnife.bind(this)

    val query = intent.getStringExtra(QUERY)

    presenter.attach(this)

    if (query.isNullOrEmpty()) {
      finish()
    } else {
      presenter.search(query)
    }

    setSupportActionBar(toolbar)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.title = query

    searchResultsRecycler.adapter = adapter
    searchResultsRecycler.emptyView = emptyView
    searchResultsRecycler.layoutManager =
      LinearLayoutManager(this)
    adapter.setOnSearchItemSelectedListener(this)
    emptyViewText.setText(R.string.no_results_found)

  }

  override fun update(searchResults: SearchResults) {
    adapter.update(searchResults)
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

  override fun albumSelected(item: MenuItem, album: Album) {
    actionHandler.albumSelected(item, album, this)
  }

  override fun albumSelected(album: Album) {
    actionHandler.albumSelected(album, this)
  }

  override fun artistSelected(item: MenuItem, artist: Artist) {
    actionHandler.artistSelected(item, artist, this)
  }

  override fun artistSelected(artist: Artist) {
    actionHandler.artistSelected(artist, this)
  }

  override fun genreSelected(item: MenuItem, genre: Genre) {
    actionHandler.genreSelected(item, genre, this)
  }

  override fun genreSelected(genre: Genre) {
    actionHandler.genreSelected(genre, this)
  }

  override fun trackSelected(item: MenuItem, track: Track) {
    actionHandler.trackSelected(item, track)
  }

  override fun trackSelected(track: Track) {
    actionHandler.trackSelected(track)
  }

  companion object {
    val QUERY = "com.kelsos.mbrc.extras.QUERY"
  }
}
