package com.kelsos.mbrc.ui.navigation.library.genreartists

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.databinding.ActivityGenreArtistsBinding
import com.kelsos.mbrc.databinding.ListEmptyViewBinding
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter.MenuItemSelectedListener
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class GenreArtistsActivity :
  BaseActivity(),
  GenreArtistsView,
  MenuItemSelectedListener {

  @Inject
  lateinit var adapter: ArtistEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: GenreArtistsPresenter

  private var genre: String? = null
  private lateinit var scope: Scope
  private lateinit var binding: ActivityGenreArtistsBinding
  private lateinit var emptyBinding: ListEmptyViewBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityGenreArtistsBinding.inflate(layoutInflater)
    emptyBinding = ListEmptyViewBinding.bind(binding.root)
    setContentView(binding.root)
    scope = Toothpick.openScopes(application, this)
    scope.installModules(
      SmoothieActivityModule(this),
      GenreArtistsModule()
    )
    Toothpick.inject(this, scope)

    genre = intent?.extras?.getString(GENRE_NAME)

    if (genre == null) {
      finish()
      return
    }

    val title = genre ?: getString(R.string.empty)
    setupToolbar(title)
    adapter.setMenuItemSelectedListener(this)
    binding.genreArtistsRecycler.adapter = adapter
    binding.genreArtistsRecycler.emptyView = emptyBinding.emptyView
    binding.genreArtistsRecycler.layoutManager = LinearLayoutManager(this)
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

  override fun onMenuItemSelected(menuItem: MenuItem, artist: Artist) {
    val action = actionHandler.artistSelected(menuItem, artist, this)
    if (action != Queue.PROFILE) {
      presenter.queue(action, artist)
    }
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler.artistSelected(artist, this)
  }

  override fun update(data: FlowCursorList<Artist>) {
    adapter.update(data)
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
    const val GENRE_NAME = "genre_name"
  }
}
