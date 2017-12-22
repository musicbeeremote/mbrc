package com.kelsos.mbrc.ui.connectionmanager

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.Button
import com.kelsos.mbrc.R
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.networking.DiscoveryStop
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ConnectionManagerActivity : BaseActivity(),
    ConnectionManagerView,
    SettingsDialogFragment.SettingsSaveListener,
    ConnectionAdapter.ConnectionChangeListener {

  @Inject lateinit var presenter: ConnectionManagerPresenter

  private val recyclerView: RecyclerView by bindView(R.id.connection_manager__connections)

  private var progress: AlertDialog? = null
  private lateinit var adapter: ConnectionAdapter
  private lateinit var scope: Scope

  private val addButton: Button by bindView(R.id.connection_manager__add)
  private val scanButton: Button by bindView(R.id.connection_manager__scan)

  private fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment.create(supportFragmentManager)
    settingsDialog.show()
  }

  private fun onScanButtonClick() {
    val builder = AlertDialog.Builder(this)
        .setTitle(R.string.progress_scanning)
        .setView(R.layout.dialog__content_progress)

    progress = builder.show()
    presenter.startDiscovery()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), ConnectionManagerModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.ui_activity_connection_manager)
    addButton.setOnClickListener { onAddButtonClick() }
    scanButton.setOnClickListener { onScanButtonClick() }

    setupToolbar(getString(R.string.connection_manager_title))

    recyclerView.setHasFixedSize(true)
    val mLayoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = mLayoutManager
    adapter = ConnectionAdapter()
    adapter.setChangeListener(this)
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
    adapter.setSelectionId(event.defaultId)
  }

  override fun onDiscoveryStopped(event: DiscoveryStopped) {

    if (progress != null) {
      progress!!.dismiss()
    }

    val message: String
    when (event.reason) {
      DiscoveryStop.NO_WIFI -> message = getString(R.string.con_man_no_wifi)
      DiscoveryStop.NOT_FOUND -> message = getString(R.string.con_man_not_found)
      DiscoveryStop.COMPLETE -> {
        message = getString(R.string.con_man_success)
        presenter.load()
      }
      else -> message = getString(R.string.unknown_reason)
    }

    Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettingsEntity) {
    presenter.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettingsEntity) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings, supportFragmentManager)
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
