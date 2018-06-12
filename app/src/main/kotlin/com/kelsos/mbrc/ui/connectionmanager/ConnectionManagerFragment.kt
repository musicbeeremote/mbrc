package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class ConnectionManagerFragment : Fragment(),
  ConnectionManagerView,
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {

  @Inject
  lateinit var presenter: ConnectionManagerPresenter

  private val recyclerView: androidx.recyclerview.widget.RecyclerView by bindView(R.id.connection_manager__connections)

  private var progress: AlertDialog? = null
  private lateinit var adapter: ConnectionAdapter
  private lateinit var scope: Scope

  private val addButton: Button by bindView(R.id.connection_manager__add)
  private val scanButton: Button by bindView(R.id.connection_manager__scan)

  private fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment.create(requireFragmentManager())
    settingsDialog.show()
  }

  private fun onScanButtonClick() {
    val builder = AlertDialog.Builder(requireContext())
      .setTitle(R.string.progress_scanning)
      .setView(R.layout.dialog__content_progress)

    progress = builder.show()
    presenter.startDiscovery()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_connection_manager, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addButton.setOnClickListener { onAddButtonClick() }
    scanButton.setOnClickListener { onScanButtonClick() }

    recyclerView.setHasFixedSize(true)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    adapter = ConnectionAdapter()
    adapter.setChangeListener(this)
    recyclerView.adapter = adapter
    presenter.attach(this)
    presenter.load()
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
    adapter.setSelectionId(event.defaultId)
  }

  override fun onDiscoveryStopped(status: Int) {
    progress?.dismiss()

    val message: String
    when (status) {
      DiscoveryStop.NO_WIFI -> message = getString(R.string.con_man_no_wifi)
      DiscoveryStop.NOT_FOUND -> message = getString(R.string.con_man_not_found)
      DiscoveryStop.COMPLETE -> {
        message = getString(R.string.con_man_success)
        presenter.load()
      }
      else -> message = getString(R.string.unknown_reason)
    }

    com.google.android.material.snackbar.Snackbar.make(recyclerView, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
  }

  override fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    com.google.android.material.snackbar.Snackbar.make(recyclerView, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettingsEntity) {
    presenter.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettingsEntity) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings, requireFragmentManager())
    settingsDialog.show()
  }

  override fun onDefault(settings: ConnectionSettingsEntity) {
    presenter.setDefault(settings)
  }

  override fun updateData(data: List<ConnectionSettingsEntity>) {
    adapter.updateData(data)
  }

  override fun updateDefault(defaultId: Long) {
    adapter.setSelectionId(defaultId)
  }
}