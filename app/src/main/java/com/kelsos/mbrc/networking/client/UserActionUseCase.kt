package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol

interface UserActionUseCase {
  fun perform(action: UserAction)
}

fun UserActionUseCase.moveTrack(request: NowPlayingMoveRequest) {
  perform(UserAction(Protocol.NowPlayingListMove, request))
}

fun UserActionUseCase.removeTrack(position: Int) {
  perform(UserAction(Protocol.NowPlayingListRemove, position))
}

fun UserActionUseCase.playTrack(position: Int) {
  perform(UserAction(Protocol.NowPlayingListPlay, position))
}
