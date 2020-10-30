package com.kelsos.mbrc.utilities

import android.app.Application
import android.content.SharedPreferences
import com.kelsos.mbrc.R
import com.kelsos.mbrc.logging.FileLoggingTree
import com.kelsos.mbrc.utilities.RemoteUtils.getVersionCode
import rx.Single
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

  override suspend fun shouldDisplayOnlyAlbumArtists(): Boolean {
    return preferences.getBoolean(getKey(R.string.settings_key_album_artists_only), false)
  }

  override fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean) {
    preferences.edit().putBoolean(getKey(R.string.settings_key_album_artists_only), onlyAlbumArtist)
      .apply()
  }

  override fun shouldShowChangeLog(): Single<Boolean> {
    return Single.fromCallable {

      val lastVersionCode = preferences.getLong(getKey(R.string.settings_key_last_version_run), 0)
      val currentVersion = context.getVersionCode()

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

}

interface SettingsManager {

  suspend fun shouldDisplayOnlyAlbumArtists(): Boolean
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Single<Boolean>
  fun isPluginUpdateCheckEnabled(): Boolean
  fun getLastUpdated(): Date
  fun setLastUpdated(lastChecked: Date)
}
