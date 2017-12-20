package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.gone
import com.kelsos.mbrc.extensions.show
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseNavigationActivity(), LyricsView {

  private val lyricsRecycler: RecyclerView by bindView(R.id.lyrics__lyrics_list)
  private val emptyView: Group by bindView(R.id.lyrics__empty_group)

  @Inject lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private lateinit var adapter: LyricsAdapter

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), LyricsModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_lyrics)

    super.setup()
    lyricsRecycler.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(this)
    lyricsRecycler.layoutManager = layoutManager
    adapter = LyricsAdapter()
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
    if (lyrics.isEmpty()) {
      emptyView.show()
    } else {
      emptyView.gone()
    }
    adapter.updateLyrics(lyrics)
  }

  override fun showNoLyrics() {
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

