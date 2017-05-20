package com.kelsos.mbrc.commands

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.SocketAction
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.events.ui.TrackRemoval
import com.kelsos.mbrc.events.ui.UpdatePosition
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.ConnectionModel
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.services.ProtocolHandler
import com.kelsos.mbrc.services.SocketService
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HandshakeCompletionActions
@Inject constructor(
    private val service: SocketService,
    private val model: MainDataModel,
    private val connectionModel: ConnectionModel,
    private val syncInteractor: LibrarySyncInteractor
) : ICommand {

  override fun execute(e: IEvent) {
    val isComplete = e.data as Boolean
    connectionModel.setHandShakeDone(isComplete)

    if (!isComplete) {
      return
    }

    if (model.pluginProtocol > 2) {
      Timber.v("Sending init request")
      service.sendData(SocketMessage.create(Protocol.INIT))
    } else {

      Timber.v("Preparing to send requests for state")

      val messages = ArrayList<SocketMessage>().apply {
        add(SocketMessage.create(Protocol.NowPlayingCover))
        add(SocketMessage.create(Protocol.PlayerStatus))
        add(SocketMessage.create(Protocol.NowPlayingTrack))
        add(SocketMessage.create(Protocol.NowPlayingLyrics))
        add(SocketMessage.create(Protocol.NowPlayingPosition))
        add(SocketMessage.create(Protocol.PluginVersion))
      }

      val totalMessages = messages.size.toLong()
      Observable.interval(150, TimeUnit.MILLISECONDS)
          .take(totalMessages)
          .subscribe({ service.sendData(messages.removeAt(0)) }) {
            Timber.v(it, "Failure while sending the init messages")
          }
    }

    syncInteractor.sync(true)
  }
}

class NotifyNotAllowedCommand
@Inject constructor(
    private val socketService: SocketService,
    private val model: ConnectionModel,
    private val handler: ProtocolHandler,
    private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    bus.post(NotifyUser(R.string.notification_not_allowed))
    socketService.socketManager(SocketAction.STOP)
    model.setConnectionState("false")
    handler.resetHandshake()
  }
}

class UpdateNowPlayingTrackMoved
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    bus.post(com.kelsos.mbrc.events.ui.TrackMoved(e.data as ObjectNode))
  }
}

class UpdateNowPlayingTrackRemoval
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    bus.post(TrackRemoval(e.data as ObjectNode))
  }
}

class UpdatePlaybackPositionCommand
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val oNode = e.data as ObjectNode
    bus.post(UpdatePosition(oNode.path("current").asInt(), oNode.path("total").asInt()))
  }
}
