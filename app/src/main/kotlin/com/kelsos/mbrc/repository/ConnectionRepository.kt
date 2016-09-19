package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.ConnectionSettings

interface ConnectionRepository {
  fun save(settings: ConnectionSettings)

  fun delete(settings: ConnectionSettings)

  fun update(settings: ConnectionSettings)

  var default: ConnectionSettings

  val all: List<ConnectionSettings>

  fun count(): Long

  val defaultId: Long
}
