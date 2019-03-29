package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentNowplayingBinding
import com.kelsos.mbrc.ui.drag.OnStartDragListener
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class NowPlayingFragment :
  Fragment(),
  OnQueryTextListener,
  OnStartDragListener,
  NowPlayingListener {

  private var _binding: FragmentNowplayingBinding? = null
  private val binding get() = _binding!!

  private val adapter: NowPlayingAdapter by lazy { NowPlayingAdapter(this@NowPlayingFragment) }

  private val viewModel: NowPlayingViewModel by viewModel()

  private var searchView: SearchView? = null
  private var searchMenuItem: MenuItem? = null
  private lateinit var touchListener: NowPlayingTouchListener
  private var itemTouchHelper: ItemTouchHelper? = null

  override fun onQueryTextSubmit(query: String): Boolean {
    closeSearch()
    return true
  }

  private fun closeSearch(): Boolean {
    searchView?.let {
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

  override fun onQueryTextChange(newText: String): Boolean {
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.nowplaying_search, menu)
    searchMenuItem = menu.findItem(R.id.now_playing_search)?.apply {
      searchView = actionView as SearchView
    }

    searchView?.apply {
      queryHint = getString(R.string.now_playing_search_hint)
      setIconifiedByDefault(true)
      setOnQueryTextListener(this@NowPlayingFragment)
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
    binding.nowPlayingTrackList.layoutManager = manager
    binding.nowPlayingTrackList.adapter = adapter
    binding.nowPlayingTrackList.itemAnimator?.changeDuration = 0
    touchListener = NowPlayingTouchListener(requireContext()) {
      if (it) {
        binding.nowPlayingRefreshLayout.isRefreshing = false
        binding.nowPlayingRefreshLayout.isEnabled = false
        binding.nowPlayingRefreshLayout.cancelPendingInputEvents()
      } else {
        binding.nowPlayingRefreshLayout.isEnabled = true
      }
    }
    binding.nowPlayingTrackList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    itemTouchHelper = ItemTouchHelper(callback).apply {
      attachToRecyclerView(binding.nowPlayingTrackList)
    }
    adapter.setListener(this)
    binding.nowPlayingRefreshLayout.setOnRefreshListener {
      this.viewModel.refresh()
    }

    viewModel.playingTrack.observe(viewLifecycleOwner) {
      adapter.setPlayingTrack(it.path)
    }

    viewModel.nowPlayingTracks.onEach {
      binding.nowPlayingRefreshLayout.isRefreshing = false
      binding.nowPlayingLoadingBar.isGone = true
      adapter.submitData(it)
      binding.nowPlayingEmptyGroup.isGone = adapter.itemCount != 0
    }.launchIn(lifecycleScope)

    viewModel.events.map { it.contentIfNotHandled }
      .filterNotNull()
      .onEach { code ->
        val messageResId = when (code) {
          1 -> R.string.refresh_failed
          else -> R.string.refresh_failed
        }
        Snackbar.make(requireView(), messageResId, Snackbar.LENGTH_SHORT).show()
      }.launchIn(lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onPress(position: Int) {
    viewModel.play(position + 1)
  }

  override fun onMove(from: Int, to: Int) {
    viewModel.moveTrack(from, to)
  }

  override fun onDismiss(position: Int) {
    viewModel.removeTrack(position)
  }

  override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
    itemTouchHelper?.startDrag(viewHolder)
  }
}
