package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.databinding.FragmentConnectionManagerBinding
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectionManagerFragment :
  Fragment(),
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {

  private val connectionManagerViewModel: ConnectionManagerViewModel by viewModel()
  private val adapter: ConnectionAdapter by inject()

  private var _binding: FragmentConnectionManagerBinding? = null
  private val binding get() = _binding!!

  private fun onAddButtonClick() {
    SettingsDialogFragment.create(parentFragmentManager).show(this)
  }

  private fun onScanButtonClick() {
    binding.connectionManagerProgress.isGone = false
    connectionManagerViewModel.startDiscovery()
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
    connectionManagerViewModel.settings.observe(viewLifecycleOwner) {
      adapter.submitList(it)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onSave(settings: ConnectionSettingsEntity) {
    connectionManagerViewModel.save(settings)
  }

  override fun onDelete(settings: ConnectionSettingsEntity) {
    connectionManagerViewModel.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettingsEntity) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings, parentFragmentManager)
    settingsDialog.show(this)
  }

  override fun onDefault(settings: ConnectionSettingsEntity) {
    connectionManagerViewModel.setDefault(settings)
  }
}
