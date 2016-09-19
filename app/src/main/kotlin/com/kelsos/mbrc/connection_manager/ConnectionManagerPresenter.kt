package com.kelsos.mbrc.connection_manager

import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.presenters.Presenter

interface ConnectionManagerPresenter : Presenter<ConnectionManagerView> {
  fun load()

  fun setDefault(settings: ConnectionSettings)

  fun save(settings: ConnectionSettings)

  fun delete(settings: ConnectionSettings)
}
