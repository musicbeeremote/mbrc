package com.kelsos.mbrc.core.data.migration

import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class MigrationManager(
  private val defaultConnectionMigration: DefaultConnectionMigration,
  private val dispatchers: AppCoroutineDispatchers
) {
  suspend fun runMigrations() {
    withContext(dispatchers.database) {
      runCatching {
        defaultConnectionMigration.migrate()
      }.onFailure { e ->
        Timber.e(e, "Migration failed")
      }
    }
  }
}
