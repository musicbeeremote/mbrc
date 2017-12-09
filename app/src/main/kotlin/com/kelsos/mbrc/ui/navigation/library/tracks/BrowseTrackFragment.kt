package com.kelsos.mbrc.ui.navigation.library.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.databinding.ListEmptyViewButtonBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryActivity.Companion.LIBRARY_SCOPE
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter.MenuItemSelectedListener
import toothpick.Toothpick
import javax.inject.Inject

class BrowseTrackFragment :
  Fragment(),
  BrowseTrackView,
  MenuItemSelectedListener {

  @Inject
  lateinit var adapter: TrackEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: BrowseTrackPresenter

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!
  private var _emptyBinding: ListEmptyViewButtonBinding? = null
  private val emptyBinding get() = _emptyBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater)
    _emptyBinding = ListEmptyViewButtonBinding.bind(binding.root)
    return binding.root
  }

  override fun search(term: String) {
    emptyBinding.listEmptySync.isGone = term.isNotEmpty()
  }

  override fun queue(success: Boolean, tracks: Int) {
    val message = if (success) {
      getString(R.string.queue_result__success, tracks)
    } else {
      getString(R.string.queue_result__failure)
    }
    Snackbar.make(requireView(), R.string.queue_result__success, Snackbar.LENGTH_SHORT)
      .setText(message)
      .show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val activity = requireActivity()
    val scope = Toothpick.openScopes(requireActivity().application, LIBRARY_SCOPE, activity, this)
    scope.installModules(BrowseTrackModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    adapter.setCoverMode(true)
    presenter.attach(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyBinding.listEmptyTitle.setText(R.string.tracks_list_empty)
    emptyBinding.listEmptySync.setOnClickListener {
      presenter.sync()
    }
    binding.libraryDataList.adapter = adapter
    binding.libraryDataList.emptyView = emptyBinding.emptyView
    binding.libraryDataList.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryDataList.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.load()
  }

  override fun update(it: List<Track>) {
    adapter.update(it)
  }

  override fun onMenuItemSelected(menuItem: MenuItem, track: Track) {
    presenter.queue(track, actionHandler.trackSelected(menuItem))
  }

  override fun onItemClicked(track: Track) {
    presenter.queue(track)
  }

  override fun showLoading() {
    emptyBinding.listEmptyIcon.visibility = View.GONE
    emptyBinding.listEmptyTitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyBinding.listEmptyIcon.visibility = View.VISIBLE
    emptyBinding.listEmptyTitle.visibility = View.VISIBLE
  }
}
