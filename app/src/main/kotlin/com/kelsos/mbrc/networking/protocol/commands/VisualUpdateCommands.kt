package com.kelsos.mbrc.networking.protocol.commands

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.SocketAction
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.TrackMoved
import com.kelsos.mbrc.events.TrackRemoval
import com.kelsos.mbrc.events.UpdatePosition
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.SocketClient
import com.kelsos.mbrc.networking.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionModel
import com.kelsos.mbrc.networking.protocol.ProtocolHandler
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import javax.inject.Inject

class HandshakeCompletionActions
@Inject
constructor(
  private val client: SocketClient,
  private val connectionModel: ConnectionModel,
  private val syncInteractor: LibrarySyncInteractor
) : ICommand {

  override fun execute(e: IEvent) {
    val isComplete = e.data as Boolean
    connectionModel.setHandShakeDone(isComplete)

    if (!isComplete) {
      return
    }

    client.sendData(SocketMessage.create(Protocol.INIT))
    client.sendData(SocketMessage.create(Protocol.PluginVersion))
    syncInteractor.sync(true)
  }
}

class NotifyNotAllowedCommand
@Inject
constructor(
  private val socketClient: SocketClient,
  private val model: ConnectionModel,
  private val handler: ProtocolHandler,
  private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    bus.post(NotifyUser(R.string.notification_not_allowed))
    socketClient.socketManager(SocketAction.STOP)
    model.setConnectionState("false")
    handler.resetHandshake()
  }
}

class UpdateNowPlayingTrackMoved
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val isSuccess: Boolean = node.path("success").asBoolean()
    val from: Int = node.path("from").asInt()
    val to: Int = node.path("to").asInt()
    bus.post(TrackMoved(from, to, isSuccess))
  }
}

class UpdateNowPlayingTrackRemoval
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val index: Int = node.path("index").asInt()
    val isSuccess: Boolean = node.path("success").asBoolean()
    bus.post(TrackRemoval(index, isSuccess))
  }
}

class UpdatePlaybackPositionCommand
@Inject constructor(private val bus: RxBus, private val mainDataModel: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val oNode = e.data as ObjectNode
    val current = oNode.path("current").asLong()
    val total = oNode.path("total").asLong()
    mainDataModel.duration = total
    mainDataModel.position = current
    bus.post(UpdatePosition(current.toInt(), total.toInt()))
  }
}
