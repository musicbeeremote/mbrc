package com.kelsos.mbrc.features.minicontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentMiniControlBinding
import org.koin.android.ext.android.inject

class MiniControlFragment : Fragment() {

  private val viewModel: MiniControlViewModel by inject()
  private lateinit var binding: FragmentMiniControlBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.viewModel = viewModel

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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_control, container, false)
    return binding.root
  }
}