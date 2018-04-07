package com.kelsos.mbrc

import com.kelsos.mbrc.interfaces.SimpleLifecycle

interface IRemoteServiceCore : SimpleLifecycle {
  fun setSyncStartAction(action: SyncStartAction?)
}