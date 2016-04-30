package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.domain.DeviceSettings
import com.kelsos.mbrc.ui.views.DeviceManagerView

interface DeviceManagerPresenter {
  fun bind(view: DeviceManagerView)

  fun onResume()

  fun onPause()

  fun saveSettings(settings: DeviceSettings)

  fun loadDevices()

  fun deleteSettings(settings: DeviceSettings)

  fun setDefault(settings: DeviceSettings)
}
