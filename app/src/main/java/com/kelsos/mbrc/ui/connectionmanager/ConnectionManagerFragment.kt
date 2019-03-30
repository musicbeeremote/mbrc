package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentConnectionManagerBinding
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectionManagerFragment : Fragment(), ConnectionAdapter.ConnectionChangeListener {

  private val viewModel: ConnectionManagerViewModel by viewModel()
  private val adapter: ConnectionAdapter by inject()

  private var _binding: FragmentConnectionManagerBinding? = null
  private val binding get() = _binding!!

  private fun onAddButtonClick() {
    SettingsDialogFragment.newInstance(parentFragmentManager).show {
      viewModel.save(it)
    }
  }

  private fun onScanButtonClick() {
    binding.connectionManagerProgress.isGone = false
    viewModel.startDiscovery()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentConnectionManagerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.connectionManagerAdd.setOnClickListener { onAddButtonClick() }
    binding.connectionManagerScan.setOnClickListener { onScanButtonClick() }

    adapter.setChangeListener(this)
    binding.connectionManagerConnections.setHasFixedSize(true)
    binding.connectionManagerConnections.layoutManager = LinearLayoutManager(requireContext())
    binding.connectionManagerConnections.adapter = adapter
    lifecycleScope.launch {
      viewModel.settings.collect {
        adapter.submitData(it)
      }
    }
    lifecycleScope.launch {
      viewModel.emitter.map { it.contentIfNotHandled }
        .filterNotNull()
        .collect { status ->
          onDiscoveryStopped(status)
        }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun onDiscoveryStopped(status: DiscoveryStop) {
    view?.findViewById<LinearProgressIndicator>(R.id.connection_manager__progress)?.isGone = true

    val message: String = when (status) {
      DiscoveryStop.NoWifi -> getString(R.string.con_man_no_wifi)
      DiscoveryStop.NotFound -> getString(R.string.con_man_not_found)
      DiscoveryStop.Complete -> {
        getString(R.string.con_man_success)
      }
    }

    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettings) {
    viewModel.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettings) {
    SettingsDialogFragment.newInstance(settings, parentFragmentManager).show {
      viewModel.save(it)
    }
  }

  override fun onDefault(settings: ConnectionSettings) {
    viewModel.setDefault(settings)
  }
}
