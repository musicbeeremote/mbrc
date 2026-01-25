package com.kelsos.mbrc.service

import android.app.Application
import android.content.Intent
import com.kelsos.mbrc.core.common.state.ConnectionStatePublisher
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.ClientConnectionUseCase
import com.kelsos.mbrc.core.networking.ConnectionCycleInfo
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Manages the foreground service lifecycle based on connection state.
 *
 * When connection is lost, this manager will attempt to reconnect multiple times
 * before giving up and stopping the service. This provides resilience against
 * temporary network issues while still cleaning up when the server is truly unavailable.
 */
interface ServiceLifecycleManager {
  /**
   * Called when connection goes offline. Starts the reconnection loop
   * which will attempt to restore connection multiple times before
   * stopping the service.
   */
  fun onConnectionLost()

  /**
   * Called when connection is restored. Cancels any pending reconnection
   * attempts and resets the retry state.
   */
  fun onConnectionRestored()

  /**
   * Called when the user intentionally disconnects. Prevents the
   * reconnection loop from starting.
   */
  fun onIntentionalDisconnect()

  /**
   * Check if the service is in the process of stopping due to connection failures.
   */
  val isStopPending: Boolean
}

class ServiceLifecycleManagerImpl(
  private val application: Application,
  private val connectionUseCase: ClientConnectionUseCase,
  private val connectionState: ConnectionStatePublisher,
  dispatchers: AppCoroutineDispatchers
) : ServiceLifecycleManager {
  private val scope = CoroutineScope(SupervisorJob() + dispatchers.main)
  private var reconnectionJob: Job? = null
  private val stopPending = AtomicBoolean(false)
  private val reconnectionCycle = AtomicInteger(0)
  private val isReconnecting = AtomicBoolean(false)
  private val intentionalDisconnect = AtomicBoolean(false)

  override val isStopPending: Boolean
    get() = stopPending.get()

  override fun onConnectionLost() {
    // Check if this was an intentional disconnect
    if (intentionalDisconnect.compareAndSet(true, false)) {
      Timber.v("Ignoring connection loss due to intentional disconnect")
      return
    }

    if (!ServiceState.isRunning || ServiceState.isStopping) {
      Timber.v("Service not running or already stopping, ignoring connection loss")
      return
    }

    // Only start reconnection loop if not already running
    if (!isReconnecting.compareAndSet(false, true)) {
      Timber.v("Reconnection loop already running, ignoring duplicate connection loss")
      return
    }

    Timber.d("Connection lost, starting reconnection loop")
    reconnectionCycle.set(0)
    startReconnectionLoop()
  }

  override fun onIntentionalDisconnect() {
    Timber.d("Intentional disconnect requested")
    intentionalDisconnect.set(true)
    // Also cancel any ongoing reconnection
    isReconnecting.set(false)
    reconnectionCycle.set(0)
    reconnectionJob?.cancel()
    reconnectionJob = null
    stopPending.set(false)
  }

  override fun onConnectionRestored() {
    val wasReconnecting = isReconnecting.getAndSet(false)
    val cycle = reconnectionCycle.getAndSet(0)

    if (wasReconnecting) {
      Timber.d("Connection restored after $cycle reconnection cycle(s)")
    }

    stopPending.set(false)
    reconnectionJob?.cancel()
    reconnectionJob = null
  }

  private fun startReconnectionLoop() {
    reconnectionJob?.cancel()
    reconnectionJob = scope.launch {
      while (isReconnecting.get() && reconnectionCycle.get() < MAX_RECONNECTION_CYCLES) {
        val cycle = reconnectionCycle.incrementAndGet()
        Timber.d(
          "Reconnection cycle $cycle/$MAX_RECONNECTION_CYCLES, " +
            "waiting ${RECONNECTION_DELAY_MS}ms before attempt"
        )

        // Update state to show we're reconnecting (before the delay)
        connectionState.updateConnection(
          ConnectionStatus.Connecting(cycle = cycle, maxCycles = MAX_RECONNECTION_CYCLES)
        )

        // Wait before attempting reconnection
        delay(RECONNECTION_DELAY_MS)

        // Check if we're still supposed to be reconnecting
        if (!isReconnecting.get()) {
          Timber.d("Reconnection cancelled during delay")
          return@launch
        }

        Timber.d("Triggering reconnection attempt (cycle $cycle)")
        connectionUseCase.connect(
          cycleInfo = ConnectionCycleInfo(cycle = cycle, maxCycles = MAX_RECONNECTION_CYCLES)
        )

        // Wait for connection attempt to complete
        // The connection manager has its own retry logic (3 attempts with backoff)
        // which takes roughly 5-10 seconds total
        delay(CONNECTION_ATTEMPT_TIMEOUT_MS)
      }

      // If we exit the loop and still reconnecting, we've exhausted all cycles
      if (isReconnecting.get()) {
        Timber.d("All $MAX_RECONNECTION_CYCLES reconnection cycles exhausted, stopping service")
        stopService()
      }
    }
  }

  private fun stopService() {
    if (stopPending.compareAndSet(false, true)) {
      Timber.d("Stopping service due to connection failure")
      isReconnecting.set(false)
      reconnectionCycle.set(0)
      reconnectionJob?.cancel()
      reconnectionJob = null
      // Set state to Offline since all reconnection attempts have failed
      connectionState.updateConnection(ConnectionStatus.Offline)
      application.stopService(Intent(application, RemoteService::class.java))
    }
  }

  companion object {
    /**
     * Maximum number of reconnection cycles before stopping the service.
     * Each cycle triggers a full connection attempt (which itself has 3 internal retries).
     */
    const val MAX_RECONNECTION_CYCLES = 3

    /**
     * Delay between reconnection cycles.
     * This gives time for network conditions to potentially improve.
     */
    const val RECONNECTION_DELAY_MS = 15_000L

    /**
     * Time to wait for a connection attempt to complete before starting next cycle.
     * The connection manager's internal retry logic takes roughly 5-10 seconds.
     */
    const val CONNECTION_ATTEMPT_TIMEOUT_MS = 12_000L
  }
}
