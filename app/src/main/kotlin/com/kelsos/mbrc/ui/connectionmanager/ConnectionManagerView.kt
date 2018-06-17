package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface ConnectionManagerView : BaseView {

  fun updateData(data: List<ConnectionSettingsEntity>)

  fun updateDefault(defaultId: Long)

  fun onDiscoveryStopped(status: Int)
}
