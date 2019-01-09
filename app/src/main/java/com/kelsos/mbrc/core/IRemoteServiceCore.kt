package com.kelsos.mbrc.core

import com.kelsos.mbrc.interfaces.SimpleLifecycle

interface IRemoteServiceCore : SimpleLifecycle {
  fun setSyncStartAction(action: SyncStartAction?)
}
