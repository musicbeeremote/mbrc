package com.kelsos.mbrc.viewmodels

import com.google.inject.Singleton
import com.kelsos.mbrc.annotations.Connection

@Singleton class ConnectionStatusModel {
  @Connection.Status var status: Long = 0
}
