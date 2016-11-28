package com.kelsos.mbrc.utilities

import android.app.Application
import android.content.SharedPreferences
import android.support.annotation.StringDef
import com.kelsos.mbrc.logging.FileLoggingTree
import rx.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager
@Inject
constructor(private val context: Application,
            private val preferences: SharedPreferences) {
  init {
    updatePreferences()
    val loggingEnabled = loggingEnabled()
    if (loggingEnabled) {
      Timber.plant(FileLoggingTree(this.context.applicationContext))
    } else {
      val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
      fileLoggingTree.let { Timber.uproot(it) }
    }
  }

  private fun loggingEnabled(): Boolean {
    return preferences.getBoolean(context.getString(R.string.settings_key_debug_logging), false)
  }

  private fun updatePreferences() {
    val enabled = preferences.getBoolean(context.getString(R.string.settings_legacy_key_reduce_volume), false)
    if (enabled) {
      preferences.edit().putString(context.getString(R.string.settings_key_incoming_call_action), REDUCE).apply()
    }
  }

  val isNotificationControlEnabled: Boolean
    get() = preferences.getBoolean(context.getString(R.string.settings_key_notification_control), true)

  internal val callAction: String
    @SuppressWarnings("WrongConstant")
    @SettingsManager.CallAction
    get() = preferences.getString(context.getString(R.string.settings_key_incoming_call_action), NONE)

  val isPluginUpdateCheckEnabled: Boolean
    get() = preferences.getBoolean(context.getString(R.string.settings_key_plugin_check), false)

  var lastUpdated: Date
    get() = Date(preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0))
    set(lastChecked) {
      val editor = preferences.edit()
      editor.putLong(context.getString(R.string.settings_key_last_update_check), lastChecked.time)
      editor.apply()
    }


  fun shouldShowPluginUpdate(): Single<Boolean> {
    return Single.fromCallable {
      val lastVersionCode = preferences.getLong(context.getString(R.string.settings_key_last_version_run), 0)
      val currentVersion = RemoteUtils.getVersionCode(context)

      if (lastVersionCode < currentVersion) {

        val editor = preferences.edit()
        editor.putLong(context.getString(R.string.settings_key_last_version_run), currentVersion)
        editor.apply()
        Timber.d("Update or fresh install")

        return@fromCallable true
      }
      return@fromCallable false
    }
  }

  @StringDef(NONE, PAUSE, STOP)
  @Retention(AnnotationRetention.SOURCE)
  internal annotation class CallAction

  companion object {

    const val NONE = "none"
    const val PAUSE = "pause"
    const val STOP = "stop"
    const val REDUCE = "reduce"
  }
}
