package com.kelsos.mbrc.ui.mini_control

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.active_status.PlayerState
import com.kelsos.mbrc.content.active_status.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.databinding.UiFragmentMiniControlBinding
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import com.squareup.picasso.Picasso
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject

class MiniControlFragment : Fragment(), MiniControlView {

  private var _binding: UiFragmentMiniControlBinding? = null
  private val binding get() = _binding!!

  @Inject
  lateinit var presenter: MiniControlPresenter

  private fun onControlClick() {
    val builder = TaskStackBuilder.create(requireContext())
    builder.addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
    builder.startActivities()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.miniControl.setOnClickListener { onControlClick() }
    binding.mcNextTrack.setOnClickListener { presenter.next() }
    binding.mcPlayPause.setOnClickListener { presenter.playPause() }
    binding.mcPrevTrack.setOnClickListener { presenter.previous() }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(MiniControlModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = UiFragmentMiniControlBinding.inflate(inflater)
    return binding.root
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
    val file = File(path)

    if (file.exists()) {
      val dimens = requireContext().getDimens()
      Picasso.get()
        .load(file)
        .noFade()
        .config(Bitmap.Config.RGB_565)
        .resize(dimens, dimens)
        .centerCrop()
        .into(binding.mcTrackCover)
    } else {
      binding.mcTrackCover.setImageResource(R.drawable.ic_image_no_cover)
    }
  }

  override fun updateTrackInfo(trackInfo: TrackInfo) {
    binding.mcTrackArtist.text = trackInfo.artist
    binding.mcTrackTitle.text = trackInfo.title
  }

  override fun updateState(@State state: String) {
    when (state) {
      PlayerState.PLAYING -> binding.mcPlayPause.setImageResource(R.drawable.ic_action_pause)
      else -> binding.mcPlayPause.setImageResource(R.drawable.ic_action_play)
    }
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}
