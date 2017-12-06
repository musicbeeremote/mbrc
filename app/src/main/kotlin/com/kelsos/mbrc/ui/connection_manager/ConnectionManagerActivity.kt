package com.kelsos.mbrc.ui.connection_manager

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.UiActivityConnectionManagerBinding
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.DiscoveryStop
import com.kelsos.mbrc.networking.StartServiceDiscoveryEvent
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.preferences.DefaultSettingsChangedEvent
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ConnectionManagerActivity :
  FontActivity(),
  ConnectionManagerView,
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {
  @Inject
  lateinit var bus: RxBus

  @Inject
  lateinit var presenter: ConnectionManagerPresenter

  private var adapter: ConnectionAdapter? = null
  private var scope: Scope? = null

  private lateinit var binding: UiActivityConnectionManagerBinding

  private fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment()
    settingsDialog.show(supportFragmentManager, "settings_dialog")
  }

  private fun onScanButtonClick() {
    binding.connectionManagerProgress.isGone = false
    bus.post(StartServiceDiscoveryEvent())
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this), ConnectionManagerModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    binding = UiActivityConnectionManagerBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val toolbar: MaterialToolbar = binding.toolbar
    val recyclerView = binding.connectionList

    binding.connectionAdd.setOnClickListener { onAddButtonClick() }
    binding.connectionScan.setOnClickListener { onScanButtonClick() }

    setSupportActionBar(toolbar)
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
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onStart() {
    super.onStart()

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setTitle(R.string.connection_manager_title)
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
    bus.register(
      this,
      ConnectionSettingsChanged::class.java,
      { this.onConnectionSettingsChange(it) },
      true
    )
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

  private fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message
    Snackbar.make(binding.connectionList, message, Snackbar.LENGTH_SHORT).show()
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
