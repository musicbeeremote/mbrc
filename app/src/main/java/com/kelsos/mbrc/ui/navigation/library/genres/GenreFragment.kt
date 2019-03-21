package com.kelsos.mbrc.ui.navigation.library.genres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.extensions.linear
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsFragmentArgs
import com.kelsos.mbrc.ui.widgets.RecyclerViewFastScroller
import com.kelsos.mbrc.utilities.nonNullObserver
import kotterknife.bindView
import org.koin.android.ext.android.inject

class GenreFragment : Fragment(),
  MenuItemSelectedListener<Genre>,
  OnRefreshListener {

  private val recycler: RecyclerView by bindView(R.id.library_browser__content)
  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.library_browser__refresh_layout)
  private val fastScroller: RecyclerViewFastScroller by bindView(R.id.fastscroller)

  private val emptyView: Group by bindView(R.id.library_browser__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.library_browser__text_title)

  private val adapter: GenreEntryAdapter by inject()
  private val actionHandler: PopupActionHandler by inject()
  private val viewModel: GenreViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyViewTitle.setText(R.string.genres_list_empty)
    swipeLayout.setOnRefreshListener(this)
    recycler.linear(adapter, fastScroller)
    recycler.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)

    viewModel.genres.nonNullObserver(this) {
      swipeLayout.isRefreshing = false

      emptyView.isVisible = it.isEmpty()
      adapter.submitList(it)
    }

    viewModel.indexes.nonNullObserver(this) {
      adapter.setIndexes(it)
    }
  }

  override fun onMenuItemSelected(action: String, item: Genre) {
    if (action == PROFILE) {
      onItemClicked(item)
      return
    }
    actionHandler.genreSelected(action, item)
  }

  override fun onItemClicked(item: Genre) {
    val args = GenreArtistsFragmentArgs.Builder(item.genre).build()
    view?.run {
      findNavController().navigate(R.id.genre_artists_fragment, args.toBundle())
    }
  }

  override fun onRefresh() {
    if (!swipeLayout.isRefreshing) {
      swipeLayout.isRefreshing = true
    }

    viewModel.reload()
  }
}