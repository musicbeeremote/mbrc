package com.kelsos.mbrc.networking.connections

import kotlinx.coroutines.flow.MutableStateFlow

class ConnectionState {
  val connection = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Off)
}
