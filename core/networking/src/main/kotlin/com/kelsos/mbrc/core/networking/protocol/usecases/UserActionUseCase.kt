package com.kelsos.mbrc.core.networking.protocol.usecases

import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.client.MessageQueue
import com.kelsos.mbrc.core.networking.client.SocketMessage
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.payloads.NowPlayingMoveRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface UserActionUseCase {
  suspend fun perform(action: UserAction)

  fun tryPerform(action: UserAction)
}

suspend fun UserActionUseCase.performUserAction(protocol: Protocol, data: Any) {
  perform(UserAction.create(protocol, data))
}

suspend fun UserActionUseCase.moveTrack(request: NowPlayingMoveRequest) {
  perform(UserAction(Protocol.NowPlayingListMove, request))
}

suspend fun UserActionUseCase.removeTrack(position: Int) {
  perform(UserAction(Protocol.NowPlayingListRemove, position))
}

suspend fun UserActionUseCase.playTrack(position: Int) {
  perform(UserAction(Protocol.NowPlayingListPlay, position))
}

suspend fun UserActionUseCase.next() {
  perform(UserAction.create(Protocol.PlayerNext))
}

suspend fun UserActionUseCase.previous() {
  perform(UserAction.create(Protocol.PlayerPrevious))
}

suspend fun UserActionUseCase.playPause() {
  perform(UserAction.create(Protocol.PlayerPlayPause))
}

suspend fun UserActionUseCase.play() {
  perform(UserAction.create(Protocol.PlayerPlay))
}

suspend fun UserActionUseCase.pause() {
  perform(UserAction.create(Protocol.PlayerPause))
}

suspend fun UserActionUseCase.playAllLibrary(shuffle: Boolean) {
  perform(UserAction.create(Protocol.LibraryPlayAll, shuffle))
}

class UserActionUseCaseImpl(
  private val messageQueue: MessageQueue,
  dispatchers: AppCoroutineDispatchers
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
