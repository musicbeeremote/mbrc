package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.mvp.BaseView

interface ConnectionManagerView : BaseView {
  fun updateModel(connectionModel: ConnectionModel)

  fun dataUpdated()
  fun onUserNotification(event: NotifyUser)
  fun onDiscoveryStopped(event: DiscoveryStopped)
  fun onConnectionSettingsChange(event: ConnectionSettingsChanged)
}
