package com.kelsos.mbrc.preferences

import com.kelsos.mbrc.features.library.PlayingTrack
import kotlinx.coroutines.flow.Flow

interface AppDataStore {
  suspend fun setClientId(clientId: String)
  suspend fun getCliendId(): String
  suspend fun updateCache(track: PlayingTrack)
  suspend fun restoreFromCache(): PlayingTrack
  suspend fun setDefaultConnectionId(id: Long)
  fun getDefaultConnectionId(): Flow<Long>
}
