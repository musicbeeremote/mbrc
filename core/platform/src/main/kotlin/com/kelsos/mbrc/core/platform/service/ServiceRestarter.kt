package com.kelsos.mbrc.core.platform.service

/**
 * Interface for restarting the remote service.
 * This abstraction allows feature modules to trigger service restarts
 * without direct dependency on the service implementation.
 */
interface ServiceRestarter {
  /**
   * Restarts the remote service.
   */
  fun restartService()
}
