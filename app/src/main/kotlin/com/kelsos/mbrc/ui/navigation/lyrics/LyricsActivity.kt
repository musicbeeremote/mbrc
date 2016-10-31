package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LyricsAdapter
import com.kelsos.mbrc.ui.activities.BaseActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class LyricsActivity : BaseActivity(), LyricsView {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  @BindView(R.id.lyrics_recycler_view) lateinit var lyricsRecycler: RecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout
  @BindView(R.id.empty_view_text) lateinit var emptyText: TextView

  @Inject lateinit var presenter: LyricsPresenter

  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lyrics)
    ButterKnife.bind(this)

    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), LyricsModule())
    Toothpick.inject(this, scope)

    super.setup()
    lyricsRecycler.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(this)
    lyricsRecycler.layoutManager = layoutManager
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
    if (lyrics.size == 1) {
      lyricsRecycler.visibility = View.GONE
      val text = lyrics[0]
      emptyText.text = if (TextUtils.isEmpty(text)) getString(R.string.no_lyrics) else text
      emptyView.visibility = View.VISIBLE
    } else {
      emptyView.visibility = View.GONE
      lyricsRecycler.visibility = View.VISIBLE
      val adapter = LyricsAdapter(lyrics)
      lyricsRecycler.adapter = adapter
    }
  }

  override fun active(): Int {
    return R.id.nav_lyrics
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}

