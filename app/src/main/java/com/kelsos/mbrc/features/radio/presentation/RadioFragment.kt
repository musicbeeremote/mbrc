package com.kelsos.mbrc.features.radio.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentRadioBinding
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter.OnRadioPressedListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RadioFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnRadioPressedListener {

  private val viewModel: RadioViewModel by viewModel()
  private val adapter: RadioAdapter by inject()

  private var _binding: FragmentRadioBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentRadioBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.radioStationsRefreshLayout.setOnRefreshListener(this)
    binding.radioStationsStationsList.adapter = adapter
    binding.radioStationsStationsList.layoutManager = LinearLayoutManager(requireContext())
    adapter.setOnRadioPressedListener(this)
    viewModel.radios.onEach {
      adapter.submitData(it)
      binding.radioStationsEmptyGroup.isGone = adapter.itemCount != 0
    }.launchIn(lifecycleScope)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onDestroy() {
    adapter.setOnRadioPressedListener(null)
    super.onDestroy()
  }

  fun error() {
    Snackbar.make(requireView(), R.string.radio__loading_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onRadioPressed(path: String) {
    viewModel.play(path)
  }

  override fun onRefresh() {
    viewModel.reload()
  }

  fun radioPlayFailed() {
    Snackbar.make(requireView(), R.string.radio__play_failed, Snackbar.LENGTH_SHORT).show()
  }

  fun radioPlaySuccessful() {
    Snackbar.make(requireView(), R.string.radio__play_successful, Snackbar.LENGTH_SHORT).show()
  }
}
