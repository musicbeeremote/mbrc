package com.kelsos.mbrc.ui.minicontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.UiFragmentMiniControlBinding
import com.kelsos.mbrc.di.inject
import toothpick.Toothpick
import javax.inject.Inject

class MiniControlFragment : Fragment(), MiniControlView {

  @Inject
  lateinit var presenter: MiniControlPresenter

  private lateinit var binding: UiFragmentMiniControlBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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
  ): View? {
    return UiFragmentMiniControlBinding.inflate(inflater, container, false).also {
      binding = it
    }.root
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun updateTrackInfo(track: PlayingTrack) {
    binding.track = track
  }

  override fun updateStatus(status: PlayerStatusModel) {
    binding.status = status
  }
}