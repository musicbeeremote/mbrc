package com.kelsos.mbrc.service

import com.kelsos.mbrc.core.networking.UserConnectPreparer

class UserConnectPreparerImpl(
  private val serviceLifecycleManager: ServiceLifecycleManager,
  private val serviceChecker: ServiceChecker
) : UserConnectPreparer {
  override fun prepare() {
    serviceLifecycleManager.onManualReconnect()
    serviceChecker.startServiceIfNotRunning()
  }
}
