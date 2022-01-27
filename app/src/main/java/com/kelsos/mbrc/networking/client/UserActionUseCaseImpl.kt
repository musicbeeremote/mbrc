package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.UserAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserActionUseCaseImpl(
  private val messageQueue: MessageQueue,
  dispatchers: AppCoroutineDispatchers,
) : UserActionUseCase {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)

  override suspend fun perform(action: UserAction) {
    messageQueue.queue(SocketMessage.create(action.protocol, action.data))
  }

  override fun tryPerform(action: UserAction) {
    scope.launch {
      perform(action)
    }
  }
}
