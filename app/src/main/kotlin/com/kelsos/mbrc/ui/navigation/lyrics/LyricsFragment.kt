package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.databinding.FragmentLyricsBinding
import com.kelsos.mbrc.di.close
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.di.scopes
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class LyricsFragment : Fragment(), LyricsView {

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private val lyricsAdapter: LyricsAdapter by lazy { LyricsAdapter() }
  private var _binding: FragmentLyricsBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentLyricsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val lyricsRecycler = binding.lyricsLyricsList
    val layoutManager = LinearLayoutManager(requireContext())
    val adapter = LyricsAdapter()
    lyricsRecycler.setHasFixedSize(true)
    lyricsRecycler.layoutManager = layoutManager
    lyricsRecycler.adapter = adapter
    presenter.attach(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
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
    binding.lyricsEmptyGroup.isGone = lyrics.isNotEmpty()
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
