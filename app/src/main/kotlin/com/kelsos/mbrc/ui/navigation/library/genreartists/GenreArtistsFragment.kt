package com.kelsos.mbrc.ui.navigation.library.genreartists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsFragmentArgs
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter
import kotterknife.bindView
import org.koin.android.ext.android.inject


class GenreArtistsFragment : Fragment(),
  GenreArtistsView,
  MenuItemSelectedListener<ArtistEntity> {

  private val recyclerView: RecyclerView by bindView(R.id.genre_artists__artist_list)
  private val emptyView: Group by bindView(R.id.genre_artists__empty_view)

  private val adapter: ArtistEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val presenter: GenreArtistsPresenter by inject()

  private lateinit var genre: String

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_genre_artists, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter.setMenuItemSelectedListener(this)
    recyclerView.linear(adapter)
    presenter.attach(this)
    presenter.load(genre)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    genre = GenreArtistsFragmentArgs.fromBundle(checkNotNull(arguments)).genre

    val title = if (genre.isEmpty()) {
      getString(R.string.empty)
    } else {
      genre
    }
  }

  override fun onMenuItemSelected(action: String, item: ArtistEntity) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.artistSelected(action, item)
  }

  override fun onItemClicked(item: ArtistEntity) {
    val args = ArtistAlbumsFragmentArgs.Builder(item.artist).build()
    findNavController(this).navigate(R.id.artist_albums_fragment, args.toBundle())
  }

  override fun update(pagedList: PagedList<ArtistEntity>) {
    adapter.submitList(pagedList)
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}