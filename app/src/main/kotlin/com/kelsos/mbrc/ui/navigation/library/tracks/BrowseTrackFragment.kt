package com.kelsos.mbrc.ui.navigation.library.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrowseTrackFragment : Fragment(), MenuItemSelectedListener<Track> {

  private val adapter: TrackEntryAdapter by inject()
  private val viewModel: BrowseTrackViewModel by viewModel()

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater)
    return binding.root
  }

  fun search(term: String) {
    binding.libraryBrowserSync.isGone = term.isNotEmpty()
  }

  fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(requireView(), R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.libraryBrowserTextTitle.setText(R.string.tracks_list_empty)
    binding.libraryBrowserSync.setOnClickListener {
      viewModel.reload()
    }
    binding.libraryBrowserContent.adapter = adapter
    binding.libraryBrowserContent.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryBrowserContent.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    adapter.setCoverMode(true)
    viewModel.tracks.onEach {
      adapter.submitData(it)
      binding.libraryBrowserEmptyGroup.isGone = adapter.itemCount != 0
    }.launchIn(lifecycleScope)
  }

  override fun onMenuItemSelected(@IdRes itemId: Int, item: Track) {
  }

  override fun onItemClicked(item: Track) {
  }
}
