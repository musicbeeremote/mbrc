package com.kelsos.mbrc.content.sync

/**
 * The class is responsible for the library metadata and playlist data network.
 */
interface LibrarySyncUseCase {
  /**
   * Starts the network process for the library and playlist metadata. The network can be
   * either manual or automatic. The automatic network should happen only under certain
   * conditions.
   *
   * @param auto Marks the network process as automatic (initiated by conditions) or
   * manual (initiated by the user)
   */
  suspend fun sync(auto: Boolean = false)

  /**
   * Provides access to the interactor's current status.
   *
   * @return Should return true if the interactor is active and running, or false if not
   */
  fun isRunning(): Boolean

  fun setOnCompleteListener(onCompleteListener: OnCompleteListener?)

  interface OnCompleteListener {
    fun onTermination()
    fun onFailure(throwable: Throwable)
    fun onSuccess()
  }
}