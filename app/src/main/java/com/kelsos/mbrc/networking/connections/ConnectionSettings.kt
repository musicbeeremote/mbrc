package com.kelsos.mbrc.networking.connections

import androidx.compose.runtime.Immutable

@Immutable
data class ConnectionSettings(
  val address: String,
  val port: Int,
  val name: String,
  val isDefault: Boolean,
  val id: Long
) {
  companion object {
    fun default(): ConnectionSettings = ConnectionSettings(
      address = "",
      port = 3000,
      name = "",
      isDefault = false,
      id = 0
    )
  }
}
