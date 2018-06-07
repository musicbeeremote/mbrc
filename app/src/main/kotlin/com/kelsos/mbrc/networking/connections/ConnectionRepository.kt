package com.kelsos.mbrc.networking.connections

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.ui.connectionmanager.ConnectionModel
import io.reactivex.Single

interface ConnectionRepository {
  fun save(settings: ConnectionSettingsEntity)

  fun delete(settings: ConnectionSettingsEntity)

  var default: ConnectionSettingsEntity?

  fun getAll(): LiveData<List<ConnectionSettingsEntity>>

  fun count(): Long

  val defaultId: Long

  fun getModel(): Single<ConnectionModel>
}