package com.kelsos.mbrc.ui.navigation.library.genres

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
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.databinding.ListEmptyViewButtonBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryActivity.Companion.LIBRARY_SCOPE
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import com.kelsos.mbrc.ui.navigation.library.genres.GenreEntryAdapter.MenuItemSelectedListener
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Toothpick
import javax.inject.Inject

class BrowseGenreFragment :
  Fragment(),
  BrowseGenreView,
  MenuItemSelectedListener {

  @Inject
  lateinit var adapter: GenreEntryAdapter
  @Inject
  lateinit var actionHandler: PopupActionHandler
  @Inject
  lateinit var presenter: BrowseGenrePresenter

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!
  private var _emptyBinding: ListEmptyViewButtonBinding? = null
  private val emptyBinding get() = _emptyBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_browse, container, false)
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
    val scope = Toothpick.openScopes(requireActivity().application, LIBRARY_SCOPE, activity, this)
    scope.installModules(BrowseGenreModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun update(cursor: FlowCursorList<Genre>) {
    adapter.update(cursor)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    emptyBinding.listEmptyTitle.setText(R.string.genres_list_empty)
    emptyBinding.listEmptySync.setOnClickListener {
      presenter.sync()
    }
    binding.libraryDataList.adapter = adapter
    binding.libraryDataList.emptyView = emptyBinding.emptyView
    binding.libraryDataList.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryDataList.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(menuItem: MenuItem, genre: Genre): Boolean {
    val action = actionHandler.genreSelected(menuItem, genre, requireActivity())
    if (action != Queue.PROFILE) {
      presenter.queue(action, genre)
    }
    return true
  }

  override fun onItemClicked(genre: Genre) {
    actionHandler.genreSelected(genre, requireActivity())
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
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
