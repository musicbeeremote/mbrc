package com.kelsos.mbrc.core

import com.kelsos.mbrc.common.SimpleLifecycle

interface IRemoteServiceCore : SimpleLifecycle {
  fun setSyncStartAction(action: SyncStartAction?)
}
