package com.kelsos.mbrc.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.MenuItem
import com.github.machinarius.preferencefragment.PreferenceFragment
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.connection_manager.ConnectionManagerActivity
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.ui.dialogs.WebViewDialog
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber

class SettingsFragment : PreferenceFragment() {
  private var bus: RxBus? = null
  private var mContext: Context? = null

  override fun onCreate(paramBundle: Bundle?) {
    super.onCreate(paramBundle)
    addPreferencesFromResource(R.xml.application_settings)
    mContext = activity

    val reduceOnIncoming = findPreference(getString(R.string.settings_key_incoming_call_action))
    val mOpenSource = findPreference(getString(R.string.preferences_open_source))
    val mManager = findPreference(resources.getString(R.string.preferences_key_connection_manager))
    val mVersion = findPreference(resources.getString(R.string.settings_version))
    val mBuild = findPreference(resources.getString(R.string.pref_key_build_time))
    val mRevision = findPreference(resources.getString(R.string.pref_key_revision))
    mOpenSource?.setOnPreferenceClickListener { preference ->
      showOpenSourceLicenseDialog()
      false
    }

    reduceOnIncoming?.setOnPreferenceChangeListener { preference, newValue ->
      if (!hasPhonePermission()) {
        requestPhoneStatePermission()
      }
      true
    }

    mManager?.setOnPreferenceClickListener { preference ->
      startActivity(Intent(mContext, ConnectionManagerActivity::class.java))
      false
    }

    if (mVersion != null) {
      try {
        mVersion.summary = String.format(resources.getString(R.string.settings_version_number),
            RemoteUtils.getVersion(mContext))
      } catch (e: PackageManager.NameNotFoundException) {
        Timber.d(e, "failed")
      }

    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      val mShowNotification = findPreference(resources.getString(R.string.settings_key_notification_control))
      mShowNotification?.setOnPreferenceChangeListener { preference, newValue ->
        val value = newValue as Boolean
        if (!value) {
          bus!!.post(MessageEvent(UserInputEventType.CancelNotification))
        }
        true
      }
    }

    val mLicense = findPreference(resources.getString(R.string.settings_key_license))
    mLicense?.setOnPreferenceClickListener { preference ->
      showLicenseDialog()
      false
    }

    if (mBuild != null) {
      mBuild.summary = BuildConfig.BUILD_TIME
    }
    if (mRevision != null) {
      mRevision.summary = BuildConfig.GIT_SHA
    }
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
