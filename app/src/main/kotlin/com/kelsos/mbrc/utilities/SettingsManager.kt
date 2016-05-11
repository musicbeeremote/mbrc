package com.kelsos.mbrc.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.text.TextUtils
import com.google.inject.Inject
import com.google.inject.Singleton
import com.kelsos.mbrc.dao.DeviceSettings
import com.kelsos.mbrc.extensions.versionCode
import com.kelsos.mbrc.repository.DeviceRepository
import rx.Observable
import rx.lang.kotlin.observable
import rx.lang.kotlin.toSingletonObservable
import timber.log.Timber
import java.util.*

@Singleton class SettingsManager
@Inject constructor(private val context: Context,
                    private val preferences: SharedPreferences,
                    private val repository: DeviceRepository,
                    private val keyProvider: KeyProvider) {
  private var isFirstRun: Boolean = false

  init {
    checkForFirstRunAfterUpdate()
  }

  private fun checkIfRemoteSettingsExist(): Boolean {
    val serverAddress = preferences.getString(keyProvider.hostKey, null)
    val serverPort: Int

    try {
      serverPort = preferences.getInt(keyProvider.portKey, 0)
    } catch (castException: ClassCastException) {
      serverPort = Integer.parseInt(preferences.getString(keyProvider.portKey, "0"))
    }

    return !(TextUtils.isEmpty(serverAddress) || serverPort == 0)
  }

  val isVolumeReducedOnRinging: Boolean
    get() = preferences.getBoolean(keyProvider.reduceVolumeKey, false)

  val isNotificationControlEnabled: Boolean
    get() = preferences.getBoolean(keyProvider.notificationKey, true)

  val isPluginUpdateCheckEnabled: Boolean
    get() = preferences.getBoolean(keyProvider.lastUpdateKey, false)

  private fun updateDefault(id: Int) {
    preferences.edit().putLong(DEFAULT_ID, id.toLong()).apply()
  }

  var lastUpdated: Date
    get() = Date(preferences.getLong(keyProvider.lastUpdateKey, 0))
    @SuppressLint("NewApi") set(lastChecked) {
      val editor = preferences.edit()
      editor.putLong(keyProvider.lastUpdateKey, lastChecked.time)
      editor.apply()
    }

  @SuppressLint("NewApi") private fun checkForFirstRunAfterUpdate() {
    try {
      val lastVersionCode = preferences.getLong(keyProvider.lastVersionKey, 0)
      val currentVersion = context.versionCode

      if (lastVersionCode < currentVersion) {
        isFirstRun = true

        val editor = preferences.edit()
        editor.putLong(keyProvider.lastVersionKey, currentVersion)

        editor.apply()

        Timber.d("load or fresh install")


      }
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.d(e, "check for first run")
    }

  }

  val observableDefault: Observable<DeviceSettings>
    get() = preferences.getLong(DEFAULT_ID, -1)
        .toSingletonObservable()
        .flatMap { id ->
          observable<DeviceSettings> {
            if (id > 0) {
              it.onNext(repository.getById(id))
            }
            it.onCompleted()
          }
        }

  val default: DeviceSettings?
    get() {
      val selection = preferences.getLong(DEFAULT_ID, -1)
      if (selection < 0) {
        return null
      } else {
        return repository.getById(selection)
      }
    }

  fun setDefault(id: Long) {
    preferences.edit().putLong(DEFAULT_ID, id).apply()
  }

  companion object {
    val DEFAULT_ID = "default_id"
  }
}
