package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.close
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.di.scopes
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class LyricsFragment : Fragment(), LyricsView {

  private val lyricsRecycler: RecyclerView by bindView(R.id.lyrics__lyrics_list)
  private val emptyView: Group by bindView(R.id.lyrics__empty_group)

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private val lyricsAdapter: LyricsAdapter by lazy { LyricsAdapter() }

  private fun setupRecycler() {
    lyricsRecycler.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(requireContext())
      adapter = lyricsAdapter
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_lyrics, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupRecycler()
    presenter.attach(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope(PRESENTER_SCOPE, { LyricsModule() })
    scope = scopes(requireActivity().application, PRESENTER_SCOPE, this)
    super.onCreate(savedInstanceState)
    scope.inject(this)
  }

  override fun onDestroy() {
    presenter.detach()
    scope.close()
    Toothpick.closeScope(PRESENTER_SCOPE)
    super.onDestroy()
  }

  override fun updateLyrics(lyrics: List<String>) {
    emptyView.isVisible = lyrics.isEmpty()
    lyricsAdapter.submitList(lyrics)
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}