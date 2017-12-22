package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface ConnectionManagerView : BaseView {

  fun updateData(data: List<ConnectionSettingsEntity>)

  fun updateDefault(defaultId: Long)

  fun onUserNotification(event: NotifyUser)

  fun onDiscoveryStopped(event: DiscoveryStopped)

  fun onConnectionSettingsChange(event: ConnectionSettingsChanged)
}
