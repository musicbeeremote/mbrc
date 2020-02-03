package com.kelsos.mbrc.features.nowplaying.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.BaseFragment
import com.kelsos.mbrc.common.ui.extensions.snackbar
import com.kelsos.mbrc.common.ui.helpers.VisibleRange
import com.kelsos.mbrc.common.ui.helpers.VisibleRangeGetter
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener
import com.kelsos.mbrc.features.nowplaying.dragsort.SimpleItemTouchHelper
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingAdapter.NowPlayingListener
import kotterknife.bindView
import org.koin.android.ext.android.inject

class NowPlayingFragment : BaseFragment() {

  private val recycler: RecyclerView by bindView(R.id.now_playing__track_list)
  private val refreshLayout: SwipeRefreshLayout by bindView(R.id.now_playing__refresh_layout)
  private val empty: Group by bindView(R.id.now_playing__empty_group)
  private val loading: ProgressBar by bindView(R.id.now_playing__loading_bar)

  private val viewModel: NowPlayingViewModel by inject()
  private val miniControlFactory: MiniControlFactory by inject()

  private var search: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private var itemTouchHelper: ItemTouchHelper? = null

  private val queryListener: OnQueryTextListener = object : OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
      closeSearch()
      viewModel.search(query)
      return true
    }

    override fun onQueryTextChange(newText: String): Boolean = true
  }

  private val dragStartListener: OnStartDragListener by lazy {
    object : OnStartDragListener {
      override fun onStartDrag(start: Boolean, viewHolder: RecyclerView.ViewHolder) {
        if (start) {
          itemTouchHelper?.startDrag(viewHolder)
          refreshLayout.apply {
            isRefreshing = false
            isEnabled = false
            cancelPendingInputEvents()
          }
        } else {
          refreshLayout.isEnabled = true
          viewModel.move()
        }
      }
    }
  }

  private val visibleRangeGetter: VisibleRangeGetter = object : VisibleRangeGetter {
    override fun visibleRange(): VisibleRange {
      val layoutManager = recycler.layoutManager as LinearLayoutManager
      val firstItem = layoutManager.findFirstVisibleItemPosition()
      val lastItem = layoutManager.findLastVisibleItemPosition()
      return VisibleRange(firstItem, lastItem)
    }
  }

  private val npAdapter: NowPlayingAdapter by lazy {
    NowPlayingAdapter(
      dragStartListener,
      nowPlayingListener,
      visibleRangeGetter
    )
  }

  private val nowPlayingListener: NowPlayingListener = object : NowPlayingListener {
    override fun onPress(position: Int) {
      viewModel.play(position)
    }

    override fun onMove(from: Int, to: Int) {
      viewModel.moveTrack(from, to)
    }

    override fun onDismiss(position: Int) {
      viewModel.removeTrack(position)
    }
  }

  private fun closeSearch(): Boolean {
    search?.let {
      if (it.isShown) {
        it.isIconified = true
        it.isFocusable = false
        it.clearFocus()
        it.setQuery("", false)
        searchMenuItem?.collapseActionView()
        return true
      }
    }
    return false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.nowplaying_search, menu)
    searchMenuItem = menu.findItem(R.id.now_playing_search)?.apply {
      search = actionView as? SearchView
    }

    search?.apply {
      queryHint = getString(R.string.now_playing_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(queryListener)
    }

    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_nowplaying, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel.trackState.observe(this) {
      npAdapter.setPlayingTrack(it.path)
      recycler.scrollToPosition(npAdapter.getPlayingTrackIndex())
    }

    viewModel.list.nonNullObserver(this) {
      refreshLayout.isRefreshing = false
      loading.isVisible = false

      empty.isVisible = it.isEmpty()
      if (npAdapter.itemCount == 0) {
        val resId = R.anim.layout_animation_from_bottom
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recycler.layoutAnimation = animation
      }
      npAdapter.submitList(it)
    }

    viewModel.emitter.nonNullObserver(this) { event ->
      if (event.hasBeenHandled) {
        return@nonNullObserver
      }

      val resId = when (event.peekContent()) {
        NowPlayingUiMessages.RefreshFailed -> R.string.now_playing__refresh_failed
        NowPlayingUiMessages.RefreshSuccess -> R.string.now_playing__refresh_success
      }
      snackbar(resId)
      refreshLayout.isRefreshing = false
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val manager = LinearLayoutManager(requireContext())
    val callback = SimpleItemTouchHelper(npAdapter)
    refreshLayout.setOnRefreshListener { viewModel.reload() }

    recycler.apply {
      layoutManager = manager
      adapter = npAdapter
      itemAnimator?.apply {
        changeDuration = 0
        removeDuration = 0
        addDuration = 0
      }
      (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    itemTouchHelper = ItemTouchHelper(callback).apply {
      attachToRecyclerView(recycler)
    }
    miniControlFactory.attach(parentFragmentManager)
  }

  override fun onBackPressed(): Boolean {
    return closeSearch()
  }
}