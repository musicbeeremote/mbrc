package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ActivityLyricsBinding
import com.kelsos.mbrc.di.close
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.di.modules
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.di.scopes
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseNavigationActivity(), LyricsView {

  override fun active(): Int = R.id.nav_lyrics

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private val lyricsAdapter: LyricsAdapter by lazy { LyricsAdapter() }

  private lateinit var binding: ActivityLyricsBinding

  private fun setupRecycler() {
    val lyricsRecycler = binding.lyricsLyricsList
    val layoutManager = LinearLayoutManager(this)
    val adapter = LyricsAdapter()
    lyricsRecycler.setHasFixedSize(true)
    lyricsRecycler.layoutManager = layoutManager
    lyricsRecycler.adapter = adapter
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope(PRESENTER_SCOPE, { LyricsModule() })
    scope = scopes(application, PRESENTER_SCOPE, this).modules(SmoothieActivityModule(this))

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lyrics)
    scope.inject(this)
    super.setup()
    setupRecycler()
    presenter.attach(this)
  }

  override fun onDestroy() {
    presenter.detach()
    scope.close()

    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
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
