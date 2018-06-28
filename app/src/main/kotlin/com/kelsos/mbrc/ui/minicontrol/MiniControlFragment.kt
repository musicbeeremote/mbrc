package com.kelsos.mbrc.ui.minicontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.UiFragmentMiniControlBinding
import org.koin.android.ext.android.inject

class MiniControlFragment : Fragment(), MiniControlView {

  private val presenter: MiniControlPresenter by inject()
  private lateinit var binding: UiFragmentMiniControlBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.attach(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return UiFragmentMiniControlBinding.inflate(inflater, container, false).also {
      binding = it
    }.root
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }

  override fun updateTrackInfo(track: PlayingTrack) {
    binding.track = track
  }

  override fun updateStatus(status: PlayerStatusModel) {
    binding.status = status
  }
}