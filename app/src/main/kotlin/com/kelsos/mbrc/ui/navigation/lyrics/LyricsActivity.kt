package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseActivity(), LyricsView {
  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  private val lyricsRecycler: EmptyRecyclerView by bindView(R.id.lyrics_recycler_view)
  private val emptyView: LinearLayout by bindView(R.id.empty_view)
  private val emptyText: TextView by bindView(R.id.empty_view_text)

  @Inject lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope
  private lateinit var adapter: LyricsAdapter

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lyrics)


    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), LyricsModule())
    Toothpick.inject(this, scope)

    super.setup()
    lyricsRecycler.setHasFixedSize(true)
    lyricsRecycler.emptyView = emptyView
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
    adapter.updateLyrics(lyrics)
  }

  override fun showNoLyrics() {
    emptyText.setText(R.string.no_lyrics)
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

