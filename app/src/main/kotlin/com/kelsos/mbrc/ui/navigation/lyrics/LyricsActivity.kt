package com.kelsos.mbrc.ui.navigation.lyrics

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.LyricsAdapter
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsView
import toothpick.Toothpick
import javax.inject.Inject

class LyricsActivity : BaseActivity(), LyricsView {

  @BindView(R.id.lyrics_recycler_view) lateinit var recyclerView: RecyclerView
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.drawer_layout) lateinit var drawer: DrawerLayout
  @BindView(R.id.navigation_view) lateinit var navigationView: NavigationView
  @Inject lateinit var adapter: LyricsAdapter
  @Inject lateinit var presenter: LyricsPresenter

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val scope = Toothpick.openScopes(application, this)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_lyrics)
    ButterKnife.bind(this)
    initialize(toolbar, drawer, navigationView)
    setCurrentSelection(R.id.drawer_menu_lyrics)

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

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun updateLyrics(lyrics: List<String>) {
    adapter.updateData(lyrics)
  }

  override fun onBackPressed() {
    ActivityCompat.finishAfterTransition(this)
  }
}
