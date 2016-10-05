package com.kelsos.mbrc.ui.activities.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.TrackEntryAdapter
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.helper.PopupActionHandler
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class AlbumTracksActivity : FontActivity(), TrackEntryAdapter.MenuItemSelectedListener {

  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.list_tracks) lateinit var listTracks: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: LinearLayout

  @Inject lateinit var adapter: TrackEntryAdapter
  @Inject lateinit var actionHandler: PopupActionHandler

  private var album: AlbumInfo? = null
  private var scope: Scope? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_album_tracks)
    ButterKnife.bind(this)
    val extras = intent.extras

    if (extras != null) {
      album = extras.getParcelable<AlbumInfo>(ALBUM)
    }

    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setDisplayShowHomeEnabled(true)

      if (TextUtils.isEmpty(album!!.album)) {
        actionBar.setTitle(R.string.non_album_tracks)
      } else {
        actionBar.title = album!!.album
      }

    }

    adapter.init(album)
    adapter.setMenuItemSelectedListener(this)
    listTracks.layoutManager = LinearLayoutManager(baseContext)
    listTracks.adapter = adapter
    listTracks.emptyView = emptyView
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId

    if (itemId == android.R.id.home) {
      finish()
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  @OnClick(R.id.play_album)
  fun onPlayClicked() {

  }

  override fun onMenuItemSelected(menuItem: MenuItem, entry: Track) {
    actionHandler.trackSelected(menuItem, entry)
  }

  override fun onItemClicked(track: Track) {
    actionHandler.trackSelected(track)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  companion object {

    val ALBUM = "albumName"
  }
}
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
