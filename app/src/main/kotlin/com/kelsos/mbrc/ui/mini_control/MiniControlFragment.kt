package com.kelsos.mbrc.ui.mini_control

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.utilities.FontUtils
import toothpick.Toothpick
import javax.inject.Inject

class MiniControlFragment : Fragment(), MiniControlView {

  @BindView(R.id.mc_track_cover) internal lateinit var trackCover: ImageView
  @BindView(R.id.mc_track_artist) internal lateinit var trackArtist: TextView
  @BindView(R.id.mc_track_title) internal lateinit var trackTitle: TextView
  @BindView(R.id.mc_play_pause) internal lateinit var playPause: ImageButton

  @Inject lateinit var presenter: MiniControlPresenter

  @OnClick(R.id.mc_next_track) fun onNextClicked() {
    presenter.onNextPressed()
  }

  @OnClick(R.id.mc_play_pause) fun onPlayPauseClicked() {
    presenter.onPlayPause()
  }

  @OnClick(R.id.mc_prev_track) fun onPreviousClicked() {
    presenter.onPreviousPressed()
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.ui_fragment_mini_control, container, false)
    ButterKnife.bind(this, view)
    val scope = Toothpick.openScopes(context.applicationContext, this)
    scope.installTestModules(MiniControlModule())
    Toothpick.inject(this, scope)
    presenter.attach(this)
    trackTitle.typeface = FontUtils.getRobotoMedium(activity)
    trackArtist.typeface = FontUtils.getRobotoRegular(activity)
    presenter.load()
    return view
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun updateCover(cover: Bitmap?) {
    if (cover != null) {
      trackCover.setImageBitmap(cover)
    } else {
      trackCover.setImageResource(R.drawable.ic_image_no_cover)
    }
  }

  override fun updateTrack(artist: String, title: String) {
    trackArtist.text = artist
    trackTitle.text = title
  }

  override fun updatePlayerState(@PlayerState.State state: String) {
    if (state == PlayerState.PLAYING) {
      playPause.setImageResource(R.drawable.ic_pause_black_36dp)
    }
    else {
      playPause.setImageResource(R.drawable.ic_play_arrow_black_36dp)
    }
  }

  companion object {

    fun newInstance(): Fragment {
      return MiniControlFragment()
    }
  }
}
