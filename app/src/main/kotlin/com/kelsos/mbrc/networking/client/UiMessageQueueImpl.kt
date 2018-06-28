package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.WeakHashMap

class UiMessageQueueImpl(
  dispatchers: AppCoroutineDispatchers
) : UiMessageQueue {
  private val publishRelay: MutableSharedFlow<UiMessage> = MutableSharedFlow(0, 5)
  private val weakHashMap: WeakHashMap<Any, Job> = WeakHashMap()
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)

  override fun dispatch(code: Int, payload: Any?) {
    publishRelay.tryEmit(UiMessage(code, payload))
  }

  override fun observe(owner: Any, observer: (UiMessage) -> Unit) {
    weakHashMap[owner] = publishRelay.onEach { observer(it) }.launchIn(scope)
  }

  override fun stop(owner: Any) {
    scope.launch {
      weakHashMap.remove(owner)?.cancelAndJoin()
    }
  }
}

data class UiMessage(val code: Int, val payload: Any?)
