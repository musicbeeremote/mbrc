package com.kelsos.mbrc.ui.preferences

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.ui.dialogs.webDialog
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

  private fun <T : Preference> preference(@StringRes resId: Int): T {
    return checkNotNull(findPreference(getString(resId)))
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.application_settings)

    val reduceOnIncoming: ListPreference = preference(R.string.settings_key_incoming_call_action)
    val openSource: Preference = preference(R.string.preferences_open_source)
    val manager: Preference = preference(R.string.preferences_key_connection_manager)
    val version: Preference = preference(R.string.settings_version)
    val mBuild: Preference = preference(R.string.pref_key_build_time)
    val mRevision: Preference = preference(R.string.pref_key_revision)
    val debugLogging: CheckBoxPreference = preference(R.string.settings_key_debug_logging)

    debugLogging.setOnPreferenceChangeListener { _, newValue ->
      if (newValue as Boolean) {
        Timber.plant(FileLoggingTree(requireContext().applicationContext))
      } else {
        val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
        fileLoggingTree?.let { Timber.uproot(it) }
      }

      true
    }

    openSource.setOnPreferenceClickListener {
      showOpenSourceLicenseDialog()
      false
    }

    reduceOnIncoming.setOnPreferenceChangeListener { _, _ ->
      if (!hasPhonePermission()) {
        requestPhoneStatePermission()
      }
      true
    }

    manager.setOnPreferenceClickListener {
      val navController = requireView().findNavController()
      navController.navigate(R.id.action_settingsFragment_to_connectionManagerFragment)
      false
    }

    try {
      val appVersion = RemoteUtils.getVersion()
      version.summary = resources.getString(R.string.settings_version_number, appVersion)
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.d(e, "failed")
    }

    val license: Preference = preference(R.string.settings_key_license)
    license.setOnPreferenceClickListener {
      showLicenseDialog()
      false
    }

    mBuild.summary = BuildConfig.BUILD_TIME
    mRevision.summary = BuildConfig.GIT_SHA
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
    webDialog(
      R.string.musicbee_remote_license_title,
      "file:///android_asset/license.html"
    )
  }

  private fun showOpenSourceLicenseDialog() {
    webDialog(
      R.string.open_source_licenses_title,
      "file:///android_asset/licenses.html"
    )
  }

  companion object {
    private const val REQUEST_CODE = 15
  }
}
