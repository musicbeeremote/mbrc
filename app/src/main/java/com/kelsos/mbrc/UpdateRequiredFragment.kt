package com.kelsos.mbrc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kelsos.mbrc.databinding.FragmentUpdateRequiredBinding

class UpdateRequiredFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    val binding = FragmentUpdateRequiredBinding.inflate(inflater, container, false)
    val version = ""
    val text = getString(R.string.plugin_update__description, version)
    binding.mainUpdateText.text = text
    binding.mainUpdateOk.setOnClickListener {
      findNavController().navigateUp()
    }
    return binding.root
  }
}
