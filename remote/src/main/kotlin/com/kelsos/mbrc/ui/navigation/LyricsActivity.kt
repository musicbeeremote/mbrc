package com.kelsos.mbrc.ui.navigation

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.Bind
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LyricsAdapter
import com.kelsos.mbrc.presenters.LyricsPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.views.LyricsView
import roboguice.RoboGuice

class LyricsActivity : BaseActivity(), LyricsView {

  @Bind(R.id.lyrics_recycler_view) lateinit var recyclerView: RecyclerView
  @Inject private lateinit var adapter: LyricsAdapter
  @Inject private lateinit var presenter: LyricsPresenter

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    RoboGuice.getInjector(this).injectMembers(this)
    setContentView(R.layout.activity_lyrics)
    initialize()
    setCurrentSelection(R.id.drawer_menu_lyrics)
    ButterKnife.bind(this)
    presenter.bind(this)
    recyclerView.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(baseContext)
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
  }

  public override fun onPause() {
    super.onPause()
    presenter.onPause()
  }

  public override fun onResume() {
    super.onResume()
    presenter.onResume()
  }

  override fun updateLyrics(lyrics: List<String>) {
    adapter.updateData(lyrics)
  }

  override fun onBackPressed() {
    ActivityCompat.finishAfterTransition(this)
  }
}
