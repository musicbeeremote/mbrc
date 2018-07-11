package com.kelsos.mbrc.ui.minicontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.databinding.FragmentMiniControlBinding
import org.koin.android.ext.android.inject

class MiniControlFragment : Fragment() {

  private val viewModel: MiniControlViewModel by inject()
  private lateinit var binding: FragmentMiniControlBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.playerStatus.observe(this) {
      binding.status = it
    }

    viewModel.playingTrack.observe(this) {
      binding.track = it
    }

    viewModel.trackPosition.observe(this) {
      binding.position = it
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return FragmentMiniControlBinding.inflate(inflater, container, false).also {
      binding = it
    }.root
  }
}