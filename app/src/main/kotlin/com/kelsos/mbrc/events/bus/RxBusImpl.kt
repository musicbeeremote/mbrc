package com.kelsos.mbrc.events.bus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.HashMap
import javax.inject.Inject

typealias EventHandler<T> = (T) -> Unit

class RxBusImpl
@Inject
constructor() : RxBus {
  private val stateFlow = MutableSharedFlow<Any>()
  private val listeners = HashMap<Any, MutableList<Job>>()
  private val supervisor = SupervisorJob()
  private val context = supervisor + Dispatchers.IO
  private val scope = CoroutineScope(context)

  init {
    Timber.v("Injecting RxBus instance %s", this)
    // TODO: This implementation has to be removed (it probably doesn't even work)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> register(receiver: Any, eventClass: Class<T>, onNext: EventHandler<T>) {
    if (listeners[receiver] == null) {
      listeners[receiver] = mutableListOf()
    }

    val eventJob = stateFlow.filter { it.javaClass === eventClass.javaClass }
      .onEach { onNext(it as T) }
      .launchIn(scope)

    checkNotNull(listeners[receiver]).add(eventJob)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> register(
    receiver: Any,
    eventClass: Class<T>,
    onNext: (T) -> Unit,
    main: Boolean
  ) {

    if (listeners[receiver] == null) {
      listeners[receiver] = mutableListOf()
    }

    val dispatcher = if (main) Dispatchers.Main else Dispatchers.IO

    val eventJob = stateFlow.filter { it.javaClass === eventClass.javaClass }
      .onEach {
        withContext(dispatcher) {
          onNext(it as T)
        }
      }
      .launchIn(scope)

    checkNotNull(listeners[receiver]).add(eventJob)
  }

  override fun unregister(receiver: Any) {
    val receiverListeners = listeners[receiver]
    if (receiverListeners !== null) {
      scope.launch {
        receiverListeners.forEach { it.cancelAndJoin() }
      }
    }
    listeners.remove(receiver)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> register(
    eventClass: Class<T>,
    onNext: (T) -> Unit,
    main: Boolean
  ) {
  }

  override fun post(event: Any) {
    stateFlow.tryEmit(event)
  }
}
