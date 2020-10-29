package com.kelsos.mbrc.ui.connection_manager

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.enums.DiscoveryStop
import com.kelsos.mbrc.events.DefaultSettingsChangedEvent
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.ui.activities.FontActivity
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class ConnectionManagerActivity : FontActivity(),
  ConnectionManagerView,
  SettingsDialogFragment.SettingsSaveListener,
  ConnectionAdapter.ConnectionChangeListener {
  @Inject
  lateinit var bus: RxBus

  @Inject
  lateinit var presenter: ConnectionManagerPresenter

  @BindView(R.id.connection_list)
  lateinit var mRecyclerView: RecyclerView

  @BindView(R.id.toolbar)
  lateinit var mToolbar: MaterialToolbar
  private var mProgress: MaterialDialog? = null
  private var mContext: Context? = null
  private var adapter: ConnectionAdapter? = null
  private var scope: Scope? = null

  @OnClick(R.id.connection_add)
  internal fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment()
    settingsDialog.show(supportFragmentManager, "settings_dialog")
  }

  @OnClick(R.id.connection_scan)
  internal fun onScanButtonClick() {
    val mBuilder = MaterialDialog.Builder(mContext!!)
    mBuilder.title(R.string.progress_scanning)
    mBuilder.content(R.string.progress_scanning_message)
    mBuilder.progress(true, 0)
    mProgress = mBuilder.show()
    bus.post(MessageEvent(UserInputEventType.StartDiscovery))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this), ConnectionManagerModule.create())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.ui_activity_connection_manager)
    ButterKnife.bind(this)
    setSupportActionBar(mToolbar)
    mRecyclerView.setHasFixedSize(true)
    val mLayoutManager = LinearLayoutManager(this)
    mRecyclerView.layoutManager = mLayoutManager
    adapter = ConnectionAdapter()
    adapter!!.setChangeListener(this)
    mRecyclerView.adapter = adapter
    presenter.attach(this)
    presenter.load()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onStart() {
    super.onStart()
    mContext = this

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

    if (mProgress != null) {
      mProgress!!.dismiss()
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

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  private fun onUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show()
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
    bus.post(DefaultSettingsChangedEvent.create())
  }

  override fun dataUpdated() {
    presenter.load()
  }
}
