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
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.BaseFragment
import com.kelsos.mbrc.common.ui.helpers.VisibleRange
import com.kelsos.mbrc.common.ui.helpers.VisibleRangeGetter
import com.kelsos.mbrc.databinding.FragmentNowplayingBinding
import com.kelsos.mbrc.features.nowplaying.dragsort.OnStartDragListener
import com.kelsos.mbrc.features.nowplaying.dragsort.SimpleItemTouchHelper
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingAdapter.NowPlayingListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NowPlayingFragment : BaseFragment() {

  private var _binding: FragmentNowplayingBinding? = null
  private val binding get() = _binding!!

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
        val swipeRefreshLayout = binding.nowPlayingRefreshLayout
        if (start) {
          itemTouchHelper?.startDrag(viewHolder)
          swipeRefreshLayout.isRefreshing = false
          swipeRefreshLayout.isEnabled = false
          swipeRefreshLayout.cancelPendingInputEvents()
        } else {
          swipeRefreshLayout.isEnabled = true
          viewModel.move()
        }
      }
    }
  }

  private val visibleRangeGetter: VisibleRangeGetter = object : VisibleRangeGetter {
    override fun visibleRange(): VisibleRange {
      val layoutManager = binding.nowPlayingTrackList.layoutManager as LinearLayoutManager
      val firstItem = layoutManager.findFirstVisibleItemPosition()
      val lastItem = layoutManager.findLastVisibleItemPosition()
      return VisibleRange(firstItem, lastItem)
    }
  }

  private val adapter: NowPlayingAdapter by lazy {
    NowPlayingAdapter(
      dragStartListener,
      nowPlayingListener,
      visibleRangeGetter
    )
  }

  private val nowPlayingListener: NowPlayingListener = object : NowPlayingListener {
    override fun onPress(position: Int) {
      viewModel.play(position + 1)
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
    _binding = FragmentNowplayingBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val manager = LinearLayoutManager(requireContext())
    val callback = SimpleItemTouchHelper(adapter)
    binding.nowPlayingRefreshLayout.setOnRefreshListener {
      this.viewModel.reload()
    }

    binding.nowPlayingTrackList.layoutManager = manager
    binding.nowPlayingTrackList.adapter = adapter
    binding.nowPlayingTrackList.itemAnimator?.apply {
      changeDuration = 0
      removeDuration = 0
      addDuration = 0
    }
    val itemAnimator = binding.nowPlayingTrackList.itemAnimator as SimpleItemAnimator
    itemAnimator.supportsChangeAnimations = false

    itemTouchHelper = ItemTouchHelper(callback).apply {
      attachToRecyclerView(binding.nowPlayingTrackList)
    }

    viewModel.trackState.observe(viewLifecycleOwner) {
      adapter.setPlayingTrack(it.path)
      binding.nowPlayingTrackList.scrollToPosition(adapter.getPlayingTrackIndex())
    }

    lifecycleScope.launch {
      adapter.loadStateFlow.drop(1).distinctUntilChangedBy { it.refresh }.collect { state ->
        if (state.refresh is LoadState.NotLoading) {
          binding.nowPlayingLoadingBar.isGone = true
        }

        binding.nowPlayingRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
        val isEmpty = state.refresh is LoadState.NotLoading && adapter.itemCount == 0
        binding.nowPlayingEmptyGroup.isGone = !isEmpty
      }
    }

    lifecycleScope.launch {
      viewModel.list.collect {
        if (adapter.itemCount == 0) {
          val resId = R.anim.layout_animation_from_bottom
          val animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
          binding.nowPlayingTrackList.layoutAnimation = animation
        }
        adapter.submitData(it)
      }
    }

    lifecycleScope.launch {
      viewModel.emitter.collect { code ->
        val messageResId = when (code) {
          NowPlayingUiMessages.RefreshFailed -> R.string.refresh_failed
          NowPlayingUiMessages.RefreshSuccess -> R.string.now_playing__refresh_success
        }
        Snackbar.make(requireView(), messageResId, Snackbar.LENGTH_SHORT).show()
      }
    }
  }

  override fun onBackPressed(): Boolean {
    return closeSearch()
  }
}
