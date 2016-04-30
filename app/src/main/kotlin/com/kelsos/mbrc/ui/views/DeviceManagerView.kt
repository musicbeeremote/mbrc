package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.DeviceSettings
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.events.ui.NotifyUser

interface DeviceManagerView {
  fun showDiscoveryResult(@DiscoveryStopped.Status reason: Long)

  fun dismissLoadingDialog()

  fun showNotification(event: NotifyUser)

  fun updateDevices(list: List<DeviceSettings>)
}
