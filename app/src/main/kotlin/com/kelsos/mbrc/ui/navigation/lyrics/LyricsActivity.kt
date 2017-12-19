package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ActivityLyricsBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseNavigationActivity(), LyricsView {

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private lateinit var adapter: LyricsAdapter

  private lateinit var binding: ActivityLyricsBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), LyricsModule())
    super.onCreate(savedInstanceState)
    binding = ActivityLyricsBinding.inflate(layoutInflater)
    setContentView(binding.root)
    Toothpick.inject(this, scope)

    super.setup()
    val lyricsRecycler = binding.lyricsLyricsList
    val layoutManager = LinearLayoutManager(this)
    adapter = LyricsAdapter()
    lyricsRecycler.setHasFixedSize(true)
    lyricsRecycler.layoutManager = layoutManager
    lyricsRecycler.adapter = adapter
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    super.onDestroy()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
    presenter.load()
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun updateLyrics(lyrics: List<String>) {
    binding.lyricsEmptyGroup.isGone = lyrics.isNotEmpty()
    adapter.updateLyrics(lyrics)
  }

  override fun showNoLyrics() {
    binding.lyricsEmptyGroup.isGone = false
    adapter.clear()
  }

  override fun active(): Int = R.id.nav_lyrics

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
