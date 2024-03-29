package com.kelsos.mbrc.networking.protocol

/**
 * Handles the step based volume changes
 */
interface VolumeModifyUseCase {
  /**
   * Increases the volume by a step up to a maximum of 100
   */
  suspend fun increment()

  /**
   * Decreases the volume by a step down to a minimum of 0
   */
  suspend fun decrement()

  /**
   * Reduces volume to 20% of the original volume
   */
  suspend fun reduceVolume()
}
