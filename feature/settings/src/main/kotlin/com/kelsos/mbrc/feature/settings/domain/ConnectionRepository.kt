package com.kelsos.mbrc.feature.settings.domain

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.data.paged
import com.kelsos.mbrc.core.data.settings.ConnectionDao
import com.kelsos.mbrc.core.data.settings.ConnectionSettingsEntity
import com.kelsos.mbrc.core.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.core.networking.discovery.RemoteServiceDiscovery
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

interface ConnectionRepository {
  suspend fun save(settings: ConnectionSettings)

  suspend fun delete(settings: ConnectionSettings)

  fun getAll(): Flow<PagingData<ConnectionSettings>>

  suspend fun count(): Long

  fun getDefault(): ConnectionSettings?

  fun setDefault(settings: ConnectionSettings)

  suspend fun discover(): DiscoveryStop
}

class ConnectionRepositoryImpl(
  private val dao: ConnectionDao,
  private val dispatchers: AppCoroutineDispatchers,
  private val discovery: RemoteServiceDiscovery
) : ConnectionRepository {
  override suspend fun discover(): DiscoveryStop {
    val discover = discovery.discover()
    if (discover is DiscoveryStop.Complete) {
      discover.settings.forEach { host ->
        // Skip hosts already saved with the same (address, port) — repeated
        // scans should not produce duplicate rows. Any other save failure
        // is logged so one bad host doesn't drop the rest of the batch.
        val existingId = withContext(dispatchers.database) {
          dao.findId(host.address, host.port)
        }
        if (existingId != null) {
          Timber.v(
            "Discovered host %s:%d already saved (id=%d)",
            host.address,
            host.port,
            existingId
          )
          return@forEach
        }
        try {
          save(host)
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          Timber.w(e, "Failed to save discovered host %s:%d", host.address, host.port)
        }
      }
    }
    return discover
  }

  override suspend fun save(settings: ConnectionSettings) {
    withContext(dispatchers.database) {
      val entity = settings.toConnectionEntity()

      if (settings.id > 0) {
        dao.update(entity)
      } else {
        dao.insert(entity)
      }

      if (count() == 1L) {
        dao.updateDefault(checkNotNull(dao.last()?.toConnection()).id)
      }
    }
  }

  override suspend fun delete(settings: ConnectionSettings) {
    withContext(dispatchers.database) {
      val oldId = settings.id
      dao.delete(settings.toConnectionEntity())
      val default = dao.getDefault()
      if (default == null && count() > 0) {
        dao.updateDefault(checkNotNull(getItemBefore(oldId) ?: dao.first()?.toConnection()).id)
      }
    }
  }

  private fun getItemBefore(id: Long): ConnectionSettings? = dao.getPrevious(id)?.toConnection()

  override fun getDefault(): ConnectionSettings? = dao.getDefault()?.toConnection()

  override fun setDefault(settings: ConnectionSettings) {
    dao.updateDefault(settings.id)
  }

  override fun getAll(): Flow<PagingData<ConnectionSettings>> = paged({ dao.getAll() }) {
    it.toConnection()
  }

  override suspend fun count(): Long = withContext(dispatchers.database) {
    dao.count()
  }
}

fun ConnectionSettingsEntity.toConnection(): ConnectionSettings = ConnectionSettings(
  address = address,
  port = port,
  name = name,
  isDefault = isDefault ?: false,
  id = id
)

fun ConnectionSettings.toConnectionEntity(): ConnectionSettingsEntity = ConnectionSettingsEntity(
  address = address,
  port = port,
  name = name,
  isDefault = isDefault,
  id = id
)
