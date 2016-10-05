package com.kelsos.mbrc.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.ArtistEntryAdapter
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.services.BrowseSync
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class BrowseArtistFragment : Fragment(), ArtistEntryAdapter.MenuItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.search_recycler_view) lateinit var recycler: EmptyRecyclerView
    @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout
    @BindView(R.id.swipe_layout) lateinit var swipeLayout: SwipeRefreshLayout

    @Inject lateinit var bus: RxBus
    @Inject lateinit var adapter: ArtistEntryAdapter
    @Inject lateinit var actionHandler: PopupActionHandler
    @Inject lateinit var sync: BrowseSync

    private var subscription: Subscription? = null
    private var scope: Scope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        scope = Toothpick.openScopes(activity.application, activity, this)
        super.onCreate(savedInstanceState)
        Toothpick.inject(this, scope)
        adapter.init(null)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_library_search, container, false)
        ButterKnife.bind(this, view)
        swipeLayout.setOnRefreshListener(this)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recycler.layoutManager = layoutManager
        adapter.setMenuItemSelectedListener(this)
        recycler.adapter = adapter
        recycler.emptyView = emptyView
    }

    override fun onMenuItemSelected(menuItem: MenuItem, entry: Artist) {
        actionHandler.artistSelected(menuItem, entry, activity)
    }

    override fun onItemClicked(artist: Artist) {
        actionHandler.artistSelected(artist, activity)
    }

    override fun onRefresh() {
        if (!swipeLayout.isRefreshing) {
            swipeLayout.isRefreshing = true
        }

        if (subscription != null && !subscription!!.isUnsubscribed) {
            return
        }

        subscription = sync.syncArtists(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { swipeLayout.isRefreshing = false }
                .subscribe({ adapter.refresh() }) {
                    bus.post(NotifyUser(R.string.refresh_failed))
                    Timber.v(it, "Failed")
                }
    }
}
