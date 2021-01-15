package com.kelsos.mbrc.preferences

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.preferences.CallAction.Companion.NONE
import com.kelsos.mbrc.preferences.CallAction.Companion.REDUCE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.util.Date

class SettingsManagerImpl(
  private val context: Application,
  private val preferences: SharedPreferences
) : SettingsManager {

  private val displayAlbumArtist: MutableStateFlow<Boolean> = MutableStateFlow(false)

  init {
    setupManager()
    displayAlbumArtist.tryEmit(
      preferences.getBoolean(
        getKey(R.string.settings_key_album_artists_only),
        false
      )
    )
  }

  private fun setupManager() {
    updatePreferences()
    val loggingEnabled = loggingEnabled()
    if (loggingEnabled) {
      Timber.plant(FileLoggingTree(this.context.applicationContext))
    } else {
      val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
      fileLoggingTree?.let { Timber.uproot(it) }
    }
  }

  private fun loggingEnabled(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_debug_logging), false)
  }

  private fun updatePreferences() {
    val enabled = preferences.getBoolean(getKey(R.string.settings_legacy_key_reduce_volume), false)
    if (enabled) {
      preferences.edit().putString(getKey(R.string.settings_key_incoming_call_action), REDUCE)
        .apply()
    }
  }

  override fun getCallAction(): CallAction = CallAction.fromString(
    preferences.getString(
      getKey(R.string.settings_key_incoming_call_action),
      NONE
    ) ?: NONE
  )

  override fun isPluginUpdateCheckEnabled(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_plugin_check), false)
  }

  override fun getLastUpdated(): Date {
    return Date(preferences.getLong(getKey(R.string.settings_key_last_update_check), 0))
  }

  override fun setLastUpdated(lastChecked: Date) {
    preferences.edit()
      .putLong(getKey(R.string.settings_key_last_update_check), lastChecked.time)
      .apply()
  }

  override fun onlyAlbumArtists(): StateFlow<Boolean> {
    return displayAlbumArtist
  }

  override fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean) {
    preferences.edit {
      putBoolean(getKey(R.string.settings_key_album_artists_only), onlyAlbumArtist)
    }
    displayAlbumArtist.tryEmit(onlyAlbumArtist)
  }

  override fun shouldShowChangeLog(): Boolean {
    val lastVersionCode = preferences.getLong(getKey(R.string.settings_key_last_version_run), 0)
    val currentVersion = RemoteUtils.getVersionCode()

    if (lastVersionCode < currentVersion) {
      preferences.edit()
        .putLong(getKey(R.string.settings_key_last_version_run), currentVersion.toLong())
        .apply()
      Timber.d("Update or fresh install")

      return true
    }
    return false
  }

  private fun getKey(settingsKey: Int) = context.getString(settingsKey)
}
