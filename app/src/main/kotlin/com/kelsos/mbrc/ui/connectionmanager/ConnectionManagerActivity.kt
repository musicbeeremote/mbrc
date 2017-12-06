package com.kelsos.mbrc.ui.connectionmanager

import android.content.Context
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
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.DiscoveryStop
import com.kelsos.mbrc.networking.StartServiceDiscoveryEvent
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.preferences.DefaultSettingsChangedEvent
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

  @Inject lateinit var bus: RxBus
  @Inject lateinit var presenter: ConnectionManagerPresenter

  private val recyclerView: RecyclerView by bindView(R.id.connection_list)

  private var progress: AlertDialog? = null
  private var mContext: Context? = null
  private var adapter: ConnectionAdapter? = null
  private var scope: Scope? = null

  private val addButton: Button by bindView(R.id.connection_add)
  private val scanButton: Button by bindView(R.id.connection_scan)

  private fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment()
    settingsDialog.show(supportFragmentManager, "settings_dialog")
  }

  private fun onScanButtonClick() {
    val builder = AlertDialog.Builder(mContext!!)
        .setTitle(R.string.progress_scanning)
        .setMessage(R.string.progress_scanning_message)

    progress = builder.show()
    bus.post(StartServiceDiscoveryEvent())
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this), ConnectionManagerModule.create())
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
    adapter!!.setChangeListener(this)
    recyclerView.adapter = adapter
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
    bus.register(this, ConnectionSettingsChanged::class.java, { this.onConnectionSettingsChange(it) }, true)
    bus.register(this, DiscoveryStopped::class.java, { this.onDiscoveryStopped(it) }, true)
    bus.register(this, NotifyUser::class.java, { this.onUserNotification(it) }, true)
  }

  override fun onPause() {
    super.onPause()
    presenter.detach()
    bus.unregister(this)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> onBackPressed()
      else -> return false
    }
    return true
  }

  override fun onSave(settings: ConnectionSettings) {
    presenter.save(settings)
  }

  private fun onConnectionSettingsChange(event: ConnectionSettingsChanged) {
    adapter!!.setSelectionId(event.defaultId)
  }

  private fun onDiscoveryStopped(event: DiscoveryStopped) {

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

  private fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDelete(settings: ConnectionSettings) {
    presenter.delete(settings)
  }

  override fun onEdit(settings: ConnectionSettings) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings)
    val fragmentManager = supportFragmentManager
    settingsDialog.show(fragmentManager, "settings_dialog")
  }

  override fun onDefault(settings: ConnectionSettings) {
    presenter.setDefault(settings)
  }

  override fun updateModel(connectionModel: ConnectionModel) {
    adapter!!.update(connectionModel)
  }

  override fun defaultChanged() {
    bus.post(DefaultSettingsChangedEvent())
  }

  override fun dataUpdated() {
    presenter.load()
  }
}
