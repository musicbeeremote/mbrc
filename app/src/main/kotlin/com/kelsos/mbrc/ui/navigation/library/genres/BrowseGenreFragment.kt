package com.kelsos.mbrc.ui.navigation.library.genres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.databinding.FragmentBrowseBinding
import com.kelsos.mbrc.ui.navigation.library.LibraryFragmentDirections
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.PopupActionHandler
import toothpick.Toothpick
import javax.inject.Inject

class BrowseGenreFragment : Fragment(), BrowseGenreView, MenuItemSelectedListener<Genre> {

  @Inject
  lateinit var adapter: GenreEntryAdapter

  @Inject
  lateinit var actionHandler: PopupActionHandler

  @Inject
  lateinit var presenter: BrowseGenrePresenter

  private var _binding: FragmentBrowseBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBrowseBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun search(term: String) {
    binding.libraryBrowserSync.isGone = term.isNotEmpty()
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
    val scope = Toothpick.openScopes(requireActivity().application, requireActivity(), this)
    scope.installModules(BrowseGenreModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
    _binding = null
  }

  override suspend fun update(genres: PagingData<Genre>) {
    adapter.submitData(genres)
    binding.libraryBrowserEmptyGroup.isGone = adapter.itemCount != 0
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.libraryBrowserTextTitle.setText(R.string.genres_list_empty)
    binding.libraryBrowserSync.setOnClickListener {
      presenter.sync()
    }
    binding.libraryBrowserContent.adapter = adapter
    binding.libraryBrowserContent.layoutManager = LinearLayoutManager(requireContext())
    binding.libraryBrowserContent.setHasFixedSize(true)
    adapter.setMenuItemSelectedListener(this)
    presenter.attach(this)
    presenter.load()
  }

  override fun onMenuItemSelected(itemId: Int, item: Genre) {
    val action = actionHandler.genreSelected(itemId)
    if (action === LibraryPopup.PROFILE) {
      onItemClicked(item)
    } else {
      presenter.queue(action, item)
    }
  }

  override fun onItemClicked(item: Genre) {
    val directions = LibraryFragmentDirections.actionLibraryFragmentToGenreArtistsActivity(
      item.genre
    )
    findNavController(this).navigate(directions)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun hideLoading() {
    binding.libraryBrowserLoadingBar.isGone = true
  }
}
