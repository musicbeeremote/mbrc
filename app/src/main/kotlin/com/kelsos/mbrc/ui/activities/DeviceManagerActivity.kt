package com.kelsos.mbrc.ui.activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.DeviceManagerAdapter
import com.kelsos.mbrc.adapters.DeviceManagerAdapter.DeviceActionListener
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.dao.DeviceSettings
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.presenters.DeviceManagerPresenter
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment
import com.kelsos.mbrc.ui.dialogs.SettingsDialogFragment.SettingsDialogListener
import com.kelsos.mbrc.ui.views.DeviceManagerView
import com.kelsos.mbrc.utilities.RxBus
import roboguice.RoboGuice

class DeviceManagerActivity : AppCompatActivity(), DeviceManagerView, SettingsDialogListener, DeviceActionListener {

  @BindView(R.id.connection_list) internal lateinit var mRecyclerView: RecyclerView
  @BindView(R.id.toolbar) internal lateinit var mToolbar: Toolbar
  @Inject private lateinit var bus: RxBus
  @Inject private lateinit var presenter: DeviceManagerPresenter
  @Inject private lateinit var adapter: DeviceManagerAdapter
  private var progressDialog: MaterialDialog? = null
  private lateinit var context: Context

  @OnClick(R.id.connection_add) fun onAddButtonClick() {
    val settingsDialog = SettingsDialogFragment()
    val args = Bundle()
    args.putInt("index", -1)
    settingsDialog.arguments = args
    settingsDialog.show(supportFragmentManager, "settings_dialog")
  }

  @OnClick(R.id.connection_scan) fun onScanButtonClick() {
    val mBuilder = MaterialDialog.Builder(context)
    mBuilder.title(R.string.progress_scanning)
    mBuilder.content(R.string.progress_scanning_message)
    mBuilder.progress(true, 0)
    progressDialog = mBuilder.show()
    bus.post(MessageEvent.newInstance(UserInputEventType.StartDiscovery))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.ui_activity_connection_manager)
    ButterKnife.bind(this)
    RoboGuice.getInjector(this).injectMembers(this)
    presenter.bind(this)
    adapter.setDeviceActionListener(this)
    setSupportActionBar(mToolbar)
    mRecyclerView.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(this)
    mRecyclerView.layoutManager = layoutManager
    mRecyclerView.adapter = adapter
    presenter.loadDevices()
  }

  override fun onStart() {
    super.onStart()
    context = this
    val actionBar = supportActionBar
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setTitle(R.string.connection_manager_title)
    }
  }

  override fun onResume() {
    super.onResume()
    presenter.onResume()
  }

  override fun onPause() {
    super.onPause()
    presenter.onPause()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> onBackPressed()
      else -> return false
    }
    return true
  }

  override fun onDialogPositiveClick(dialog: SettingsDialogFragment, settings: DeviceSettings) {
    presenter.saveSettings(settings)
  }

  override fun showDiscoveryResult(@DiscoveryStopped.Status reason: Long) {

    val message: String
    if (reason == DiscoveryStopped.NO_WIFI) {
      message = getString(R.string.con_man_no_wifi)
    } else if (reason == DiscoveryStopped.NOT_FOUND) {
      message = getString(R.string.con_man_not_found)
    } else if (reason == DiscoveryStopped.SUCCESS) {
      message = getString(R.string.con_man_success)
    } else {
      message = getString(R.string.unknown_reason)
    }

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun dismissLoadingDialog() {
    if (progressDialog != null && progressDialog!!.isShowing) {
      progressDialog!!.dismiss()
    }
  }

  override fun showNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun updateDevices(list: List<DeviceSettings>) {
    adapter.updateDevices(list)
  }

  override fun onDelete(settings: DeviceSettings) {
    presenter.deleteSettings(settings)
  }

  override fun onDefault(settings: DeviceSettings) {
    presenter.setDefault(settings)
  }

  override fun onEdit(settings: DeviceSettings) {
    val settingsDialog = SettingsDialogFragment.newInstance(settings)
    settingsDialog.show(supportFragmentManager, SettingsDialogFragment.TAG)
  }
}
