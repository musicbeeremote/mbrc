package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious

interface UserActionUseCase {
  suspend fun perform(action: UserAction)

  fun tryPerform(action: UserAction)
}

fun UserActionUseCase.perform(
  protocol: Protocol,
  data: Any,
) {
  tryPerform(UserAction.create(protocol, data))
}

suspend fun UserActionUseCase.performUserAction(
  protocol: Protocol,
  data: Any,
) {
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
  perform(UserAction.create(PlayerNext))
}

suspend fun UserActionUseCase.previous() {
  perform(UserAction.create(PlayerPrevious))
}

suspend fun UserActionUseCase.playPause() {
  perform(UserAction.create(PlayerPlayPause))
}
