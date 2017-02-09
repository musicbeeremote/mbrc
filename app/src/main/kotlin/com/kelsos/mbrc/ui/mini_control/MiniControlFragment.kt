package com.kelsos.mbrc.ui.mini_control

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import com.squareup.picasso.Picasso
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject

class MiniControlFragment : Fragment(), MiniControlView {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  @BindView(R.id.mc_track_cover) lateinit var trackCover: ImageView
  @BindView(R.id.mc_track_artist) lateinit var trackArtist: TextView
  @BindView(R.id.mc_track_title) lateinit var trackTitle: TextView
  @BindView(R.id.mc_play_pause) lateinit var playPause: ImageButton

  @Inject
  lateinit var presenter: MiniControlPresenter

  @OnClick(R.id.mini_control)
  internal fun onControlClick() {
    val builder = TaskStackBuilder.create(requireContext())
    builder.addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
    builder.startActivities()
  }

  @OnClick(R.id.mc_next_track)
  internal fun onNextClick() {
    presenter.next()
  }

  @OnClick(R.id.mc_play_pause)
  internal fun onPlayClick() {
    presenter.playPause()
  }

  @OnClick(R.id.mc_prev_track)
  internal fun onPreviousClick() {
    presenter.previous()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(PRESENTER_SCOPE).installModules(MiniControlModule())
    val scope = Toothpick.openScopes(requireActivity().application, PRESENTER_SCOPE, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.ui_fragment_mini_control, container, false)
    ButterKnife.bind(this, view)
    return view
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

  override fun updateCover(path: String) {
    if (activity == null) {
      return
    }

    val file = File(path)

    if (file.exists()) {
      val dimens = requireContext().getDimens()
      Picasso.get()
        .load(file)
        .noFade()
        .config(Bitmap.Config.RGB_565)
        .resize(dimens, dimens)
        .centerCrop()
        .into(trackCover)

    } else {
      trackCover.setImageResource(R.drawable.ic_image_no_cover)
    }
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

  override fun onDestroy() {
    Toothpick.closeScope(this)
    Toothpick.closeScope(PRESENTER_SCOPE)
    super.onDestroy()
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}
