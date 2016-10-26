package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.data.dao.ConnectionSettings

interface ConnectionManagerPresenter {
  fun bind(view: ConnectionManagerView)

  fun onResume()

  fun onPause()

  fun saveSettings(settings: ConnectionSettings)

  fun loadDevices()

  fun deleteSettings(settings: ConnectionSettings)

  fun setDefault(settings: ConnectionSettings)
}
