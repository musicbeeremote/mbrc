package com.kelsos.mbrc.utilities

import android.app.Application
import android.content.SharedPreferences
import android.support.annotation.StringDef
import com.kelsos.mbrc.R
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.utilities.SettingsManager.CallAction
import com.kelsos.mbrc.utilities.SettingsManager.Companion.NONE
import com.kelsos.mbrc.utilities.SettingsManager.Companion.REDUCE
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManagerImpl
@Inject
constructor(
    private val context: Application,
    private val preferences: SharedPreferences
) : SettingsManager {

  init {
    setupManager()
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

  override fun getClientId(): String {
    var clientId = preferences.getString(CLIENT_ID, "")

    if (clientId.isBlank()) {
      clientId = UUID.randomUUID().toString()
      preferences.edit().putString(CLIENT_ID, clientId).apply()
    }

    return clientId
  }

  private fun loggingEnabled(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_debug_logging), false)
  }

  private fun updatePreferences() {
    val enabled = preferences.getBoolean(getKey(R.string.settings_legacy_key_reduce_volume), false)
    if (enabled) {
      preferences.edit().putString(getKey(R.string.settings_key_incoming_call_action), REDUCE).apply()
    }
  }

  override fun isNotificationControlEnabled(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_notification_control), true)
  }

  @CallAction override fun getCallAction(): String = preferences.getString(
      getKey(R.string.settings_key_incoming_call_action), NONE)

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

  override fun shouldDisplayOnlyAlbumArtists(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable preferences.getBoolean(getKey(R.string.settings_key_album_artists_only), false)
    }
  }

  override fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean) {
    preferences.edit().putBoolean(getKey(R.string.settings_key_album_artists_only), onlyAlbumArtist).apply()
  }

  override fun shouldShowChangeLog(): Single<Boolean> {
    return Single.fromCallable {

      val lastVersionCode = preferences.getLong(getKey(R.string.settings_key_last_version_run), 0)
      val currentVersion = RemoteUtils.getVersionCode(context)

      if (lastVersionCode < currentVersion) {
        preferences.edit()
            .putLong(getKey(R.string.settings_key_last_version_run), currentVersion)
            .apply()
        Timber.d("Update or fresh install")

        return@fromCallable true
      }
      return@fromCallable false
    }
  }

  private fun getKey(settingsKey: Int) = context.getString(settingsKey)

  companion object {
    const val CLIENT_ID = "com.kelsos.mbrc.CLIENT_ID"
  }
}

interface SettingsManager {

  fun shouldDisplayOnlyAlbumArtists(): Single<Boolean>
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Single<Boolean>
  fun isNotificationControlEnabled(): Boolean
  fun isPluginUpdateCheckEnabled(): Boolean

  @CallAction fun getCallAction(): String

  @StringDef(NONE,
      PAUSE,
      STOP,
      REDUCE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class CallAction

  companion object {
    const val NONE = "none"
    const val PAUSE = "pause"
    const val STOP = "stop"
    const val REDUCE = "reduce"
  }

  fun getLastUpdated(): Date
  fun setLastUpdated(lastChecked: Date)
  fun getClientId(): String
}
