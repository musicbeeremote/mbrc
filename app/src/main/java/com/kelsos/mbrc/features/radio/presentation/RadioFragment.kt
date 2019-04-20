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
      binding.radioStationsLoadingBar.isGone = true
      binding.radioStationsRefreshLayout.isRefreshing = false
    }.launchIn(lifecycleScope)

    viewModel.emitter.onEach { event ->
      val resId = when (event) {
        RadioUiMessages.QueueFailed -> R.string.radio__queue_failed
        RadioUiMessages.QueueSuccess -> R.string.radio__queue_success
        RadioUiMessages.NetworkError -> R.string.radio__queue_network_error
        RadioUiMessages.RefreshSuccess -> R.string.radio__refresh_success
        RadioUiMessages.RefreshFailed -> R.string.radio__refresh_failed
      }
      Snackbar.make(requireView(), resId, Snackbar.LENGTH_SHORT).show()
      binding.radioStationsRefreshLayout.isRefreshing = false
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onDestroy() {
    adapter.setOnRadioPressedListener(null)
    super.onDestroy()
  }

  override fun onRadioPressed(path: String) {
    viewModel.play(path)
  }

  override fun onRefresh() {
    viewModel.reload()
  }
}
