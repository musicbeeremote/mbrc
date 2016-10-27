package com.kelsos.mbrc.ui.mini_control

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.TaskStackBuilder
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
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import com.squareup.picasso.Picasso
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject

class MiniControlFragment : Fragment(), MiniControlView {

  @BindView(R.id.mc_track_cover) lateinit var trackCover: ImageView
  @BindView(R.id.mc_track_artist) lateinit var trackArtist: TextView
  @BindView(R.id.mc_track_title) lateinit var trackTitle: TextView
  @BindView(R.id.mc_play_pause) lateinit var playPause: ImageButton

  @Inject lateinit var bus: RxBus
  @Inject lateinit var presenter: MiniControlPresenter

  private fun post(action: String) {
    bus.post(MessageEvent.action(UserAction.create(action)))
  }

  @OnClick(R.id.mini_control)
  internal fun onControlClick() {
    val builder = TaskStackBuilder.create(context)
    builder.addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
    builder.startActivities()
  }

  @OnClick(R.id.mc_next_track)
  internal fun onNextClick() {
    val playerNext = Protocol.PlayerNext
    post(playerNext)
  }

  @OnClick(R.id.mc_play_pause)
  internal fun onPlayClick() {
    post(Protocol.PlayerPlayPause)
  }

  @OnClick(R.id.mc_prev_track)
  internal fun onPreviousClick() {
    post(Protocol.PlayerPrevious)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(activity.application, activity, this)
    scope.installModules(MiniControlModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.ui_fragment_mini_control, container, false)
    ButterKnife.bind(this, view)
    return view
  }

  override fun onPause() {
    super.onPause()
    bus.unregister(this)
  }

  override fun onResume() {
    super.onResume()
    bus.register(this, CoverChangedEvent::class.java, { this.onCoverChange(it) }, true)
    bus.register(this, TrackInfoChangeEvent::class.java, { this.onTrackInfoChange(it) }, true)
    bus.register(this, PlayStateChange::class.java, { this.onPlayStateChange(it) }, true)
    presenter.attach(this)
    presenter.load()
  }

  override fun updateCover(cover: String) {
    val file = File(cover)
    val dimens = context.getDimens()

    Picasso.with(context).invalidate(file)
    Picasso.with(context)
        .load(file)
        .placeholder(R.drawable.ic_image_no_cover)
        .config(Bitmap.Config.RGB_565)
        .resize(dimens, dimens)
        .centerCrop()
        .into(trackCover)
  }

  override fun updateTrackInfo(trackInfo: TrackInfo) {
    trackArtist.text = trackInfo.artist
    trackTitle.text = trackInfo.title
  }

  override fun updateState(@State state: String) {
    when (state) {
      PlayerState.PLAYING -> playPause.setImageResource(R.drawable.ic_action_pause)
      else -> playPause.setImageResource(R.drawable.ic_action_play)
    }
  }

  private fun onCoverChange(event: CoverChangedEvent) {
    if (!event.available) {
      trackCover.setImageResource(R.drawable.ic_image_no_cover)
      return
    }
    updateCover(event.path)
  }

  private fun onTrackInfoChange(event: TrackInfoChangeEvent) {
    updateTrackInfo(event.trackInfo)
  }

  private fun onPlayStateChange(event: PlayStateChange) {
    updateState(event.state)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
