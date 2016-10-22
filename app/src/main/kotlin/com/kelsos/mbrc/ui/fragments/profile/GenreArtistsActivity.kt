package com.kelsos.mbrc.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistAdapter
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.extensions.empty
import com.kelsos.mbrc.presenters.GenreArtistsPresenter
import com.kelsos.mbrc.ui.views.GenreArtistView
import roboguice.RoboGuice

class GenreArtistsActivity : AppCompatActivity(), GenreArtistView, ArtistAdapter.MenuItemSelectedListener {
  @BindView(R.id.genre_artists_recycler) internal lateinit var recyclerView: RecyclerView
  @BindView(R.id.toolbar) internal lateinit var toolbar: Toolbar

  @Inject private lateinit var adapter: ArtistAdapter
  @Inject private lateinit var presenter: GenreArtistsPresenter
  private var genreId: Long = 0

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_genre_artists)
    RoboGuice.getInjector(this).injectMembers(this)
    ButterKnife.bind(this)
    presenter.bind(this)
    val manager = LinearLayoutManager(this)
    recyclerView.layoutManager = manager
    recyclerView.adapter = adapter
    adapter.setMenuItemSelectedListener(this)
    val extras = intent.extras
    genreId = 0

    var title = String.empty

    if (extras != null) {
      genreId = extras.getLong(GENRE_ID, 0)
      title = extras.getString(GENRE_NAME, String.empty)
    }

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.title = title
    presenter.load(genreId)
  }

  override fun update(data: List<Artist>) {
    adapter.updateData(data)
  }

  override fun onQueueSuccess() {

  }

  override fun onQueueFailure() {

  }

  private fun openProfile(artist: Artist) {
    val intent = Intent(this, ArtistAlbumsActivity::class.java)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_ID, artist.id)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.name)
    startActivity(intent)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    val itemId = menuItem.itemId
    when (itemId) {
      R.id.popup_artist_play -> presenter.queue(Queue.NOW, entry)
      R.id.popup_artist_album -> openProfile(entry)
      R.id.popup_artist_queue_next -> presenter.queue(Queue.NEXT, entry)
      R.id.popup_artist_queue_last -> presenter.queue(Queue.LAST, entry)
      R.id.popup_artist_playlist -> {
      }
    }
  }

  override fun onItemClicked(artist: Artist) {
    openProfile(artist)
  }

  companion object {

    const val GENRE_ID = "genre_id"
    const val GENRE_NAME = "genre_name"
  }
}// Required empty public constructor
