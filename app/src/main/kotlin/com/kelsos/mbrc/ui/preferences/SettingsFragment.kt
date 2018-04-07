package com.kelsos.mbrc.ui.preferences

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerActivity
import com.kelsos.mbrc.ui.dialogs.WebViewDialog
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {


  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.application_settings)

    val reduceOnIncoming = findPreference(getString(R.string.settings_key_incoming_call_action))
    val mOpenSource = findPreference(getString(R.string.preferences_open_source))
    val mManager = findPreference(resources.getString(R.string.preferences_key_connection_manager))
    val mVersion = findPreference(resources.getString(R.string.settings_version))
    val mBuild = findPreference(resources.getString(R.string.pref_key_build_time))
    val mRevision = findPreference(resources.getString(R.string.pref_key_revision))
    val debugLogging = findPreference(resources.getString(R.string.settings_key_debug_logging))

    debugLogging?.setOnPreferenceChangeListener { _, newValue ->
      if (newValue as Boolean) {
        Timber.plant(FileLoggingTree(requireContext().applicationContext))
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

    reduceOnIncoming?.setOnPreferenceChangeListener { _, _ ->
      if (!hasPhonePermission()) {
        requestPhoneStatePermission()
      }
      true
    }

    mManager?.setOnPreferenceClickListener {
      startActivity(Intent(requireContext(), ConnectionManagerActivity::class.java))
      false
    }

    try {
      val version = RemoteUtils.getVersion()
      mVersion?.summary = resources.getString(R.string.settings_version_number, version)
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.d(e, "failed")
    }

    val showNotification =
      findPreference(resources.getString(R.string.settings_key_notification_control))

    showNotification?.setOnPreferenceChangeListener { _, newValue ->
      val value = newValue as Boolean
      if (!value) {
        //todo remove notification

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

  private fun requestPhoneStatePermission() {
    requireActivity().run {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_PHONE_STATE),
        REQUEST_CODE
      )
    }
  }

  private fun hasPhonePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(
      requireContext(),
      Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun showLicenseDialog() {
    showWebViewDialog(
      "file:///android_asset/license.html",
      R.string.musicbee_remote_license_title,
      "license_dialog"
    )
  }

  private fun showOpenSourceLicenseDialog() {
    showWebViewDialog(
      "file:///android_asset/licenses.html",
      R.string.open_source_licenses_title,
      "licenses_dialogs"
    )
  }

  private fun showWebViewDialog(url: String, @StringRes titleResId: Int, tag: String) {
    val dialog = WebViewDialog()
    dialog.arguments = Bundle().apply {
      putString(WebViewDialog.ARG_URL, url)
      putInt(WebViewDialog.ARG_TITLE, titleResId)
    }
    requireActivity().let {
      dialog.show(it.supportFragmentManager, tag)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
    android.R.id.home -> {
      activity?.finish()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }


  companion object {
    private const val REQUEST_CODE = 15

    fun newInstance(): SettingsFragment = SettingsFragment()
  }
}