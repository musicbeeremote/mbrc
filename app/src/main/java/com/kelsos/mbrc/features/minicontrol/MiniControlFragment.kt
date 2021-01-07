package com.kelsos.mbrc.features.minicontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentMiniControlBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MiniControlFragment : Fragment() {
  private val viewModel: MiniControlViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentMiniControlBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_mini_control,
      container,
      false
    )
    binding.viewModel = viewModel

    viewModel.playerStatus.observe(viewLifecycleOwner) {
      binding.status = it
    }

    viewModel.playingTrack.observe(viewLifecycleOwner) {
      binding.track = it
    }

    viewModel.trackPosition.observe(viewLifecycleOwner) {
      binding.position = it
    }
    return binding.root
  }
}
