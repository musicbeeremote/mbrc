package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentConnectionManagerBinding
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class ConnectionManagerFragment :
  Fragment(),
  ConnectionManagerView,
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {

  @Inject
  lateinit var presenter: ConnectionManagerPresenter

  private var adapter: ConnectionAdapter? = null
  private lateinit var scope: Scope

  private var _binding: FragmentConnectionManagerBinding? = null
  private val binding get() = _binding!!

  private fun onAddButtonClick() {
    SettingsDialogFragment.create(parentFragmentManager).show()
  }

  private fun onScanButtonClick() {
    binding.connectionManagerProgress.isGone = false
    presenter.startDiscovery()
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

    adapter = ConnectionAdapter()
    adapter?.setChangeListener(this)
    binding.connectionManagerConnections.setHasFixedSize(true)
    binding.connectionManagerConnections.layoutManager = LinearLayoutManager(requireContext())
    binding.connectionManagerConnections.adapter = adapter
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, this)
    scope.installModules(ConnectionManagerModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onSave(settings: ConnectionSettingsEntity) {
    presenter.save(settings)
  }

  override fun onConnectionSettingsChange(event: ConnectionSettingsChanged) {
    checkNotNull(adapter).setSelectionId(event.defaultId)
  }

  override fun onDiscoveryStopped(status: Int) {
    binding.connectionManagerProgress.isGone = true

    val message: String = when (status) {
      DiscoveryStop.NO_WIFI -> getString(R.string.con_man_no_wifi)
      DiscoveryStop.NOT_FOUND -> getString(R.string.con_man_not_found)
      DiscoveryStop.COMPLETE -> {
        presenter.load()
        getString(R.string.con_man_success)
      }
      else -> throw IllegalArgumentException(status.toString())
    }

    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message
    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettingsEntity) {
    presenter.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettingsEntity) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings, parentFragmentManager)
    settingsDialog.show()
  }

  override fun onDefault(settings: ConnectionSettingsEntity) {
    presenter.setDefault(settings)
  }

  override fun updateData(data: List<ConnectionSettingsEntity>) {
    checkNotNull(adapter).updateData(data)
  }

  override fun updateDefault(defaultId: Long) {
    checkNotNull(adapter).setSelectionId(defaultId)
  }
}
