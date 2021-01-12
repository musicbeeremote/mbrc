package com.kelsos.mbrc.features.radio.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.snackbar
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.databinding.FragmentRadioBinding
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter.OnRadioPressedListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RadioFragment : Fragment(), OnRadioPressedListener {
  private val viewModel: RadioViewModel by viewModel()
  private val adapter: RadioAdapter by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentRadioBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_radio,
      container,
      false
    )

    val swipeLayout = binding.radioStationsRefreshLayout
    val radioView = binding.radioStationsStationsList
    val emptyViewTitle = binding.radioStationsTextTitle
    val emptyViewIcon = binding.radioStationsEmptyIcon
    val emptyView = binding.radioStationsEmptyGroup
    val emptyViewProgress = binding.radioStationsLoadingBar

    swipeLayout.setOnRefreshListener { viewModel.reload() }
    radioView.adapter = adapter
    radioView.layoutManager = LinearLayoutManager(requireContext())
    emptyViewTitle.setText(R.string.radio__no_radio_stations)
    emptyViewIcon.setImageResource(R.drawable.ic_radio_black_80dp)
    adapter.setOnRadioPressedListener(this)

    viewModel.radios.nonNullObserver(viewLifecycleOwner) { list ->
      emptyView.isVisible = list.isEmpty()
      emptyViewProgress.isVisible = false
      swipeLayout.isRefreshing = false
      adapter.submitList(list)
    }

    viewModel.emitter.nonNullObserver(viewLifecycleOwner) { event ->
      event.contentIfNotHandled?.run {
        val resId = when (this) {
          RadioUiMessages.QueueFailed -> R.string.radio__queue_failed
          RadioUiMessages.QueueSuccess -> R.string.radio__queue_success
          RadioUiMessages.NetworkError -> R.string.radio__queue_network_error
          RadioUiMessages.RefreshSuccess -> R.string.radio__refresh_success
          RadioUiMessages.RefreshFailed -> R.string.radio__refresh_failed
        }
        snackbar(resId)
        swipeLayout.isRefreshing = false
      }
    }
    return binding.root
  }

  override fun onDestroy() {
    adapter.setOnRadioPressedListener(null)
    super.onDestroy()
  }

  override fun onRadioPressed(path: String) {
    viewModel.play(path)
  }
}
