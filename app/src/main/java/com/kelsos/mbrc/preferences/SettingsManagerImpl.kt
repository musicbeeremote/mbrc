package com.kelsos.mbrc.preferences

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils.getVersionCode
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.preferences.CallAction.Companion.NONE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.time.Instant

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

  override fun getCallAction(): CallAction = CallAction.fromString(
    preferences.getString(
      getKey(R.string.settings_key_incoming_call_action),
      NONE
    ) ?: NONE
  )

  override fun isPluginUpdateCheckEnabled(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_plugin_check), false)
  }

  override fun getLastUpdated(required: Boolean): Instant {
    val key = if (required) REQUIRED_CHECK else getKey(R.string.settings_key_last_update_check)
    return Instant.ofEpochMilli(preferences.getLong(key, 0))
  }

  override fun setLastUpdated(lastChecked: Instant, required: Boolean) {
    val key = if (required) REQUIRED_CHECK else getKey(R.string.settings_key_last_update_check)
    preferences.edit()
      .putLong(key, lastChecked.toEpochMilli())
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
    val key = getKey(R.string.settings_key_last_version_run)
    val lastVersionCode = preferences.getLong(key, 0)
    val currentVersion = getVersionCode().toLong()

    if (lastVersionCode < currentVersion) {
      preferences.edit()
        .putLong(getKey(R.string.settings_key_last_version_run), currentVersion)
        .apply()
      Timber.d("Update or fresh install")

      return true
    }
    return false
  }

  private fun getKey(settingsKey: Int) = context.getString(settingsKey)

  companion object {
    const val REQUIRED_CHECK = "update_required_check"
  }
}
