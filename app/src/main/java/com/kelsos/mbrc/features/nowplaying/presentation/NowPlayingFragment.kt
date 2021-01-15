package com.kelsos.mbrc.features.nowplaying.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
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
import com.kelsos.mbrc.databinding.FragmentNowplayingBinding
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener
import com.kelsos.mbrc.features.nowplaying.dragsort.SimpleItemTouchHelper
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingAdapter.NowPlayingListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class NowPlayingFragment : BaseFragment() {
  private lateinit var recycler: RecyclerView
  private lateinit var refreshLayout: SwipeRefreshLayout

  private val viewModel: NowPlayingViewModel by viewModel()

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
  ): View {
    val binding: FragmentNowplayingBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_nowplaying,
      container,
      false
    )

    refreshLayout = binding.nowPlayingRefreshLayout
    recycler = binding.nowPlayingTrackList

    viewModel.trackState.observe(viewLifecycleOwner) {
      npAdapter.setPlayingTrack(it.path)
      recycler.scrollToPosition(npAdapter.getPlayingTrackIndex())
    }

    viewModel.list.nonNullObserver(viewLifecycleOwner) {
      refreshLayout.isRefreshing = false
      binding.nowPlayingLoadingBar.isVisible = false
      binding.nowPlayingEmptyGroup.isVisible = it.isEmpty()
      if (npAdapter.itemCount == 0) {
        val resId = R.anim.layout_animation_from_bottom
        val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        recycler.layoutAnimation = animation
      }
      npAdapter.submitList(it)
    }

    viewModel.emitter.nonNullObserver(viewLifecycleOwner) { event ->
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
    return binding.root
  }

  override fun onBackPressed(): Boolean {
    return closeSearch()
  }
}
