package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.networking.connections.ConnectionSettings

interface ConnectionManagerPresenter : Presenter<ConnectionManagerView> {
  fun load()

  fun setDefault(settings: ConnectionSettings)

  fun save(settings: ConnectionSettings)

  fun delete(settings: ConnectionSettings)

  fun startDiscovery()
}
