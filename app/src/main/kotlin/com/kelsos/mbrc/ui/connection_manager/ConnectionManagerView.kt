package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.data.dao.ConnectionSettings
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.events.ui.NotifyUser

interface ConnectionManagerView {
  fun showDiscoveryResult(@DiscoveryStopped.Status reason: Long)

  fun dismissLoadingDialog()

  fun showNotification(event: NotifyUser)

  fun updateDevices(list: List<ConnectionSettings>)
}
