package com.kelsos.mbrc.networking.connections

data class ConnectionSettings(
  var address: String,
  var port: Int,
  var name: String,
  var isDefault: Boolean,
  var id: Long
)
