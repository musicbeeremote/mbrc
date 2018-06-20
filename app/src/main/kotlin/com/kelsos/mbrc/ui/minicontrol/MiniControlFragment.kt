package com.kelsos.mbrc.ui.minicontrol

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.UiFragmentMiniControlBinding
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.extensions.getDimens
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
    findNavController().navigate(R.id.main_navigation_fragment)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.miniControl.setOnClickListener { onControlClick() }
    binding.mcNextTrack.setOnClickListener { presenter.next() }
    binding.mcPlayPause.setOnClickListener { presenter.playPause() }
    binding.mcPrevTrack.setOnClickListener { presenter.previous() }
    presenter.attach(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(miniControlModule)
    super.onCreate(savedInstanceState)
    scope.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = UiFragmentMiniControlBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
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

  override fun updateTrackInfo(track: PlayingTrack) {
    binding.mcTrackArtist.text = track.artist
    binding.mcTrackTitle.text = track.title
    updateCover(track.coverUrl)
  }

  override fun updateStatus(status: PlayerStatusModel) {
    when (status.state) {
      PlayerState.PLAYING -> binding.mcPlayPause.setImageResource(R.drawable.ic_action_pause)
      else -> binding.mcPlayPause.setImageResource(R.drawable.ic_action_play)
    }
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
