package com.kelsos.mbrc.core.data.migration

import android.content.SharedPreferences
import androidx.core.content.edit
import com.kelsos.mbrc.core.data.settings.ConnectionDao
import timber.log.Timber

/**
 * Migrates the default connection setting from SharedPreferences to the database.
 * This is a one-time migration that runs after database migration 3->4.
 * Must be called from a database dispatcher context.
 */
class DefaultConnectionMigration(
  private val sharedPreferences: SharedPreferences,
  private val connectionDao: ConnectionDao
) {
  suspend fun migrate(): Boolean {
    if (!sharedPreferences.contains(OLD_DEFAULT_KEY)) {
      Timber.d("No default connection key found in SharedPreferences, nothing to migrate")
      return false
    }

    val defaultId = sharedPreferences.getLong(OLD_DEFAULT_KEY, -1L)
    if (defaultId == -1L) {
      Timber.w("Default connection ID is invalid, nothing to migrate")
      sharedPreferences.edit { remove(OLD_DEFAULT_KEY) }
      return false
    }

    val connection = connectionDao.getById(defaultId)
    if (connection != null) {
      connectionDao.updateDefault(defaultId)
      Timber.d("Migrated default connection ID $defaultId to database")
    } else {
      Timber.w("Default connection ID $defaultId not found in database")
    }

    sharedPreferences.edit { remove(OLD_DEFAULT_KEY) }
    Timber.d("Removed old default connection key from SharedPreferences")
    return connection != null
  }

  companion object {
    private const val OLD_DEFAULT_KEY = "mbrc_default_settings"
  }
}
