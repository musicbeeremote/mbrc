package com.kelsos.mbrc.features.player

import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.UserAction
import com.kelsos.mbrc.networking.protocol.UserActionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface IPlayerActions {
  val playPause: () -> Unit
  val previous: () -> Unit
  val next: () -> Unit
  val stop: () -> Unit
  val shuffle: () -> Unit
  val repeat: () -> Unit
  val mute: () -> Unit
  val changeVolume: (Int) -> Unit
  val seek: (Int) -> Unit
  val toggleFavorite: () -> Unit
  val toggleScrobbling: () -> Unit
}

class PlayerActions(
  private val userActionUseCase: UserActionUseCase,
  private val scope: CoroutineScope,
  private val progressRelay: MutableSharedFlow<Int>,
  private val volumeRelay: MutableSharedFlow<Int>
) : IPlayerActions {

  override val playPause: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerPlayPause, true))
    }
  }

  override val previous: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerPrevious, true))
    }
  }

  override val next: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerNext, true))
    }
  }

  override val stop: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
    }
  }

  override val shuffle: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerShuffle))
    }
  }

  override val repeat: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerRepeat))
    }
  }

  override val mute: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
    }
  }

  override val changeVolume: (Int) -> Unit = { volume ->
    scope.launch {
      volumeRelay.emit(volume)
    }
  }

  override val seek: (Int) -> Unit = { position ->
    scope.launch {
      progressRelay.emit(position)
    }
  }

  override val toggleFavorite: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.NowPlayingLfmRating))
    }
  }

  override val toggleScrobbling: () -> Unit = {
    scope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerScrobble))
    }
  }
}
