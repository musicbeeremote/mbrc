package com.kelsos.mbrc.ui.activities.nav

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
import com.kelsos.mbrc.events.ui.LyricsUpdatedEvent
import com.kelsos.mbrc.presenters.LyricsPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.views.LyricsView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class LyricsActivity : BaseActivity(), LyricsView {
  @Inject lateinit var presenter: LyricsPresenter

  @BindView(R.id.lyrics_recycler_view) lateinit var lyricsRecycler: RecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout
  @BindView(R.id.empty_view_text) lateinit var emptyText: TextView
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_lyrics)
    ButterKnife.bind(this)
    super.setup()
    lyricsRecycler.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(this)
    lyricsRecycler.layoutManager = layoutManager
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
    bus.register(this, LyricsUpdatedEvent::class.java, { this.onLyricsUpdated(it) }, true)
    presenter.load()
  }

  override fun onPause() {
    super.onPause()
    bus.unregister(this)
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

  private fun onLyricsUpdated(update: LyricsUpdatedEvent) {
    presenter.updateLyrics(update.lyrics)
  }

  override fun active(): Int {
    return R.id.nav_lyrics
  }
}
