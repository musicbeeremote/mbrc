package com.kelsos.mbrc.ui.navigation.library.artists

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.adapters.ArtistEntryAdapter.MenuItemSelectedListener
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment : Fragment(),
    BrowseArtistView,
    MenuItemSelectedListener,
    OnRefreshListener {

  @BindView(R.id.library_data_list) lateinit var recycler: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyTitle: TextView

  @Inject lateinit var adapter: ArtistEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler
  @Inject lateinit var presenter: BrowseArtistPresenter

  private var scope: Scope? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, activity, this)
    scope?.installModules(BrowseArtistModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
    adapter.refresh()
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_library_search, container, false)
    ButterKnife.bind(this, view)
    emptyTitle.setText(R.string.artists_list_empty)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler.setHasFixedSize(true)
    recycler.adapter = adapter
    recycler.emptyView = emptyView
    recycler.layoutManager = LinearLayoutManager(recycler.context)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
    actionHandler.artistSelected(menuItem, entry, requireActivity())
  }

  override fun onItemClicked(artist: Artist) {
    actionHandler.artistSelected(artist, requireActivity())
  }

  override fun onRefresh() {
    presenter.reload()
  }

  override fun update(data: FlowCursorList<Artist>) {
    adapter.update(data)
  }

  override fun failure(throwable: Throwable) {
    Snackbar.make(recycler, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }
}
