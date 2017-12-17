package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.UiActivityConnectionManagerBinding
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.networking.DiscoveryStop
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ConnectionManagerActivity :
  BaseActivity(),
  ConnectionManagerView,
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {

  @Inject
  lateinit var presenter: ConnectionManagerPresenter

  private var adapter: ConnectionAdapter? = null
  private lateinit var scope: Scope

  private lateinit var binding: UiActivityConnectionManagerBinding

  private fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment()
    settingsDialog.show(supportFragmentManager, "settings_dialog")
  }

  private fun onScanButtonClick() {
    binding.connectionManagerProgress.isGone = false
    presenter.startDiscovery()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), ConnectionManagerModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = UiActivityConnectionManagerBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val recyclerView = binding.connectionList

    binding.connectionAdd.setOnClickListener { onAddButtonClick() }
    binding.connectionScan.setOnClickListener { onScanButtonClick() }

    setupToolbar(getString(R.string.connection_manager_title))

    recyclerView.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    adapter = ConnectionAdapter()
    adapter!!.setChangeListener(this)
    recyclerView.adapter = adapter
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroy() {
    presenter.detach()
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> onBackPressed()
      else -> return false
    }
    return true
  }

  override fun onSave(settings: ConnectionSettingsEntity) {
    presenter.save(settings)
  }

  override fun onConnectionSettingsChange(event: ConnectionSettingsChanged) {
    adapter!!.setSelectionId(event.defaultId)
  }

  override fun onDiscoveryStopped(event: DiscoveryStopped) {
    binding.connectionManagerProgress.isGone = true

    val message: String = when (event.reason) {
      DiscoveryStop.NO_WIFI -> getString(R.string.con_man_no_wifi)
      DiscoveryStop.NOT_FOUND -> getString(R.string.con_man_not_found)
      DiscoveryStop.COMPLETE -> {
        presenter.load()
        getString(R.string.con_man_success)
      }
      else -> throw IllegalArgumentException(event.reason.toString())
    }

    Snackbar.make(binding.connectionList, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message
    Snackbar.make(binding.connectionList, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettingsEntity) {
    presenter.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettingsEntity) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings)
    val fragmentManager = supportFragmentManager
    settingsDialog.show(fragmentManager, "settings_dialog")
  }

  override fun onDefault(settings: ConnectionSettingsEntity) {
    presenter.setDefault(settings)
  }

  override fun updateModel(connectionModel: ConnectionModel) {
    adapter!!.update(connectionModel)
  }

  override fun dataUpdated() {
    presenter.load()
  }
}
