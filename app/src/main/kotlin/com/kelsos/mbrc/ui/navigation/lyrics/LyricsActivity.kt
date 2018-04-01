package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import androidx.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.close
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.di.modules
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.di.scopes
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseNavigationActivity(), LyricsView {

  private val lyricsRecycler: RecyclerView by bindView(R.id.lyrics__lyrics_list)
  private val emptyView: Group by bindView(R.id.lyrics__empty_group)

  override fun active(): Int = R.id.nav_lyrics

  @Inject
  lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private val lyricsAdapter: LyricsAdapter by lazy { LyricsAdapter() }

  private fun setupRecycler() {
    lyricsRecycler.apply {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(this@LyricsActivity)
      adapter = lyricsAdapter
    }
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