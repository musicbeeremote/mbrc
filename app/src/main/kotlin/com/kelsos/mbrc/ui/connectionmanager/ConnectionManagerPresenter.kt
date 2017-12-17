package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface ConnectionManagerPresenter : Presenter<ConnectionManagerView> {
  fun load()

  fun setDefault(settings: ConnectionSettingsEntity)

  fun save(settings: ConnectionSettingsEntity)

  fun delete(settings: ConnectionSettingsEntity)

  fun startDiscovery()
}
