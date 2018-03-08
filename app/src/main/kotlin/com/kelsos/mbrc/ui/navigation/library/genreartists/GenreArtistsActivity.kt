package com.kelsos.mbrc.ui.navigation.library.genreartists

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.databinding.ActivityGenreArtistsBinding
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class GenreArtistsActivity : BaseActivity(), GenreArtistsView, MenuItemSelectedListener<Artist> {

  @Inject
  lateinit var adapter: ArtistEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: GenreArtistsPresenter

  private var genre: String? = null
  private lateinit var scope: Scope
  private lateinit var binding: ActivityGenreArtistsBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), GenreArtistsModule())
    super.onCreate(savedInstanceState)
    binding = ActivityGenreArtistsBinding.inflate(layoutInflater)
    setContentView(binding.root)
    Toothpick.inject(this, scope)

    genre = intent?.extras?.getString(GENRE_NAME)

    if (genre == null) {
      finish()
      return
    }

    val title = genre ?: getString(R.string.empty)
    setupToolbar(title)
    adapter.setMenuItemSelectedListener(this)
    binding.genreArtistsArtistList.adapter = adapter
    binding.genreArtistsArtistList.layoutManager = LinearLayoutManager(this)
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

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Artist) {
    val action = actionHandler.artistSelected(itemId, item, this)
    if (action != LibraryPopup.PROFILE) {
      presenter.queue(action, item)
    }
  }

  override fun onItemClicked(item: Artist) {
    actionHandler.artistSelected(item, this)
  }

  override suspend fun update(artists: PagingData<Artist>) {
    adapter.submitData(artists)
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
