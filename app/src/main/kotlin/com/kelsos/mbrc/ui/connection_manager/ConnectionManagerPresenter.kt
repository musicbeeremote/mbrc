package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.data.dao.DeviceSettings

interface ConnectionManagerPresenter {
  fun bind(view: ConnectionManagerView)

  fun onResume()

  fun onPause()

  fun saveSettings(settings: DeviceSettings)

  fun loadDevices()

  fun deleteSettings(settings: DeviceSettings)

  fun setDefault(settings: DeviceSettings)
}
