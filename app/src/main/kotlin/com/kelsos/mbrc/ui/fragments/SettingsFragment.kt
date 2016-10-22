package com.kelsos.mbrc.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import javax.inject.Inject
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.extensions.version
import com.kelsos.mbrc.ui.activities.DeviceManagerActivity
import com.kelsos.mbrc.ui.dialogs.WebViewDialog
import com.kelsos.mbrc.utilities.RxBus
import roboguice.RoboGuice
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

  @Inject private lateinit var bus: RxBus

  override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.application_settings)
    RoboGuice.getInjector(activity).injectMembers(this)

    val mOpenSource = findPreference(resources.getString(R.string.preferences_open_source))
    val mManager = findPreference(resources.getString(R.string.preferences_key_connection_manager))
    val mVersion = findPreference(resources.getString(R.string.settings_version))
    val mBuild = findPreference(resources.getString(R.string.pref_key_build_time))
    val mRevision = findPreference(resources.getString(R.string.pref_key_revision))

    mOpenSource?.setOnPreferenceClickListener {
      showOpenSourceLicenseDialog()
      false
    }

    mManager?.setOnPreferenceClickListener {
      startActivity(Intent(activity, DeviceManagerActivity::class.java))
      false
    }

    try {
      mVersion?.summary = String.format(resources.getString(R.string.settings_version_number),
          context.version)
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.d(e, "Name not found")
    }

    val mShowNotification = findPreference(resources.getString(R.string.settings_key_notification_control))
    mShowNotification?.setOnPreferenceChangeListener { preference, newValue ->
      val value = newValue as Boolean
      if (!value) {
        bus.post(MessageEvent.newInstance(UserInputEventType.CancelNotification))
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

  companion object {

    fun newInstance(): SettingsFragment {
      val fragment: SettingsFragment = SettingsFragment()
      return fragment
    }
  }
}
