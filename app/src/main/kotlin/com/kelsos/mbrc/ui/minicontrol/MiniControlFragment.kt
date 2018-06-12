package com.kelsos.mbrc.ui.minicontrol

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.ui.navigation.main.MainFragment
import com.squareup.picasso.Picasso
import kotterknife.bindView
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject

class MiniControlFragment : androidx.fragment.app.Fragment(), MiniControlView {

  private val trackCover: ImageView by bindView(R.id.mc_track_cover)
  private val trackArtist: TextView by bindView(R.id.mc_track_artist)
  private val trackTitle: TextView by bindView(R.id.mc_track_title)
  private val playPause: ImageButton by bindView(R.id.mc_play_pause)

  private val miniControl: View by bindView(R.id.mini_control)
  private val nextButton: ImageButton by bindView(R.id.mc_next_track)
  private val previousButton: ImageButton by bindView(R.id.mc_prev_track)

  @Inject
  lateinit var presenter: MiniControlPresenter

  private fun onControlClick() {
    val context = context ?: error("null context")
    val builder = TaskStackBuilder.create(context)
    builder.addNextIntentWithParentStack(Intent(context, MainFragment::class.java))
    builder.startActivities()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    miniControl.setOnClickListener { onControlClick() }
    nextButton.setOnClickListener { presenter.next() }
    playPause.setOnClickListener { presenter.playPause() }
    previousButton.setOnClickListener { presenter.previous() }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val context = activity ?: error("null context")
    Toothpick.openScope(PRESENTER_SCOPE).installModules(MiniControlModule())
    val scope = Toothpick.openScopes(context.application, PRESENTER_SCOPE, this)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.ui_fragment_mini_control, container, false)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  private fun updateCover(path: String) {
    val context = context ?: return
    val file = File(path)

    if (file.exists()) {

      val dimens = context.getDimens()
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

  override fun updateTrackInfo(track: PlayingTrackModel) {
    trackArtist.text = track.artist
    trackTitle.text = track.title
    updateCover(track.coverUrl)
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

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}