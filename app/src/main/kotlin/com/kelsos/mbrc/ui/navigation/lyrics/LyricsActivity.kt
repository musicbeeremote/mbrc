package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ActivityLyricsBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseNavigationActivity(), LyricsView {
  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private lateinit var adapter: LyricsAdapter

  private lateinit var binding: ActivityLyricsBinding

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLyricsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), LyricsModule())
    Toothpick.inject(this, scope)

    super.setup()
    val lyricsRecycler: EmptyRecyclerView = binding.lyricsRecyclerView
    val layoutManager = LinearLayoutManager(this)
    adapter = LyricsAdapter()
    lyricsRecycler.setHasFixedSize(true)
    lyricsRecycler.emptyView = binding.emptyView
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
    adapter.updateLyrics(lyrics)
  }

  override fun showNoLyrics() {
    binding.emptyViewText.setText(R.string.no_lyrics)
    adapter.clear()
  }

  override fun active(): Int {
    return R.id.nav_lyrics
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}
