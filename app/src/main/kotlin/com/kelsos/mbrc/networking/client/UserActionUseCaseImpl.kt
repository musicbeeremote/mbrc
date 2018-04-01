package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.events.UserAction
import javax.inject.Inject

class UserActionUseCaseImpl
@Inject
constructor(private val messageQueue: MessageQueue) : UserActionUseCase {
  override fun perform(action: UserAction) {
    messageQueue.queue(SocketMessage.create(action.context, action.data))
  }
}
