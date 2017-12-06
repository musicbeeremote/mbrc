package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.mvp.BaseView

interface ConnectionManagerView : BaseView {
  fun updateModel(connectionModel: ConnectionModel)

  fun defaultChanged()

  fun dataUpdated()
}
