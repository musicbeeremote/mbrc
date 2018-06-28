package com.kelsos.mbrc.ui.navigation.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.databinding.FragmentRadioBinding
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import org.koin.android.ext.android.inject

class RadioFragment :
  Fragment(),
  RadioView,
  SwipeRefreshLayout.OnRefreshListener,
  OnRadioPressedListener {

  private val presenter: RadioPresenter by inject()
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
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onDestroy() {
    presenter.detach()
    adapter.setOnRadioPressedListener(null)
    super.onDestroy()
  }

  override suspend fun update(data: PagingData<RadioStation>) {
    adapter.submitData(data)
    binding.radioStationsEmptyGroup.isGone = adapter.itemCount != 0
  }

  override fun error(error: Throwable) {
    Snackbar.make(requireView(), R.string.radio__loading_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onRadioPressed(path: String) {
    presenter.play(path)
  }

  override fun onRefresh() {
    presenter.refresh()
  }

  override fun radioPlayFailed() {
    Snackbar.make(requireView(), R.string.radio__play_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun radioPlaySuccessful() {
    Snackbar.make(requireView(), R.string.radio__play_successful, Snackbar.LENGTH_SHORT).show()
  }

  override fun loading(visible: Boolean) {
    if (!visible) {
      binding.radioStationsLoadingBar.isGone = true
      binding.radioStationsRefreshLayout.isRefreshing = false
    }
  }
}
