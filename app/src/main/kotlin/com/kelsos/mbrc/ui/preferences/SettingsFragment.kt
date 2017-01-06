package com.kelsos.mbrc.ui.preferences

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.ui.connection_manager.ConnectionManagerActivity
import com.kelsos.mbrc.ui.dialogs.WebViewDialog
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {
  private var bus: RxBus? = null

  private lateinit var mContext: Context

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.application_settings)
    mContext = activity

    val reduceOnIncoming = findPreference(getString(R.string.settings_key_incoming_call_action))
    val mOpenSource = findPreference(getString(R.string.preferences_open_source))
    val mManager = findPreference(resources.getString(R.string.preferences_key_connection_manager))
    val mVersion = findPreference(resources.getString(R.string.settings_version))
    val mBuild = findPreference(resources.getString(R.string.pref_key_build_time))
    val mRevision = findPreference(resources.getString(R.string.pref_key_revision))
    val debugLogging = findPreference(resources.getString(R.string.settings_key_debug_logging))

    debugLogging?.setOnPreferenceChangeListener { preference, newValue ->
      if (newValue as Boolean) {
        Timber.plant(FileLoggingTree(context.applicationContext))
      } else {
        val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
        fileLoggingTree?.let { Timber.uproot(it) }
      }

      true
    }

    mOpenSource?.setOnPreferenceClickListener {
      showOpenSourceLicenseDialog()
      false
    }

    reduceOnIncoming?.setOnPreferenceChangeListener { preference, newValue ->
      if (!hasPhonePermission()) {
        requestPhoneStatePermission()
      }
      true
    }

    mManager?.setOnPreferenceClickListener {
      startActivity(Intent(mContext, ConnectionManagerActivity::class.java))
      false
    }

    try {
      val version = RemoteUtils.getVersion(mContext)
      mVersion?.summary = resources.getString(R.string.settings_version_number, version)
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.d(e, "failed")
    }

    val showNotification = findPreference(resources.getString(R.string.settings_key_notification_control))

    showNotification?.setOnPreferenceChangeListener { preference, newValue ->
      val value = newValue as Boolean
      if (!value) {
        bus!!.post(MessageEvent(UserInputEventType.CancelNotification))
      }
      true
    }

    val mLicense = findPreference(resources.getString(R.string.settings_key_license))
    mLicense?.setOnPreferenceClickListener {
      showLicenseDialog()
      false
    }

    mBuild?.summary = BuildConfig.BUILD_TIME
    mRevision?.summary = BuildConfig.GIT_SHA
  }


  fun requestPhoneStatePermission() {
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE)
  }

  fun hasPhonePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
  }

  private fun showLicenseDialog() {
    val args = Bundle()
    args.putString(WebViewDialog.ARG_URL, "file:///android_asset/license.html")
    args.putInt(WebViewDialog.ARG_TITLE, R.string.musicbee_remote_license_title)
    val dialog = WebViewDialog()
    dialog.arguments = args
    dialog.show(activity.supportFragmentManager, "license_dialog")
  }

  private fun showOpenSourceLicenseDialog() {
    val args = Bundle()
    args.putString(WebViewDialog.ARG_URL, "file:///android_asset/licenses.html")
    args.putInt(WebViewDialog.ARG_TITLE, R.string.open_source_licenses_title)
    val dialog = WebViewDialog()
    dialog.arguments = args
    dialog.show(activity.supportFragmentManager, "licenses_dialogs")
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      android.R.id.home -> {
        activity.finish()
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  fun setBus(bus: RxBus) {
    this.bus = bus
  }

  companion object {

    private val REQUEST_CODE = 15

    fun newInstance(bus: RxBus): SettingsFragment {
      val fragment = SettingsFragment()
      fragment.setBus(bus)
      return fragment
    }
  }
}
