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
import com.kelsos.mbrc.networking.connections.ConnectionStatusModel
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HandshakeCompletionActions
@Inject constructor(
    private val client: SocketClient,
    private val model: MainDataModel,
    private val syncInteractor: LibrarySyncInteractor
) : ICommand {

  override fun execute(e: IEvent) {
    val isComplete = e.data as Boolean

    if (!isComplete) {
      return
    }

    if (model.pluginProtocol > 2) {
      Timber.v("Sending init request")
      client.sendData(SocketMessage.create(Protocol.INIT))
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
          .subscribe({ client.sendData(messages.removeAt(0)) }) {
            Timber.v(it, "Failure while sending the init messages")
          }
    }

    syncInteractor.sync(true)
  }
}

class NotifyNotAllowedCommand
@Inject constructor(
    private val socketClient: SocketClient,
    private val statusModel: ConnectionStatusModel,
    private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    bus.post(NotifyUser(R.string.notification_not_allowed))
    socketClient.socketManager(SocketAction.STOP)
    statusModel.disconnected()
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
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val oNode = e.data as ObjectNode
    bus.post(UpdatePosition(oNode.path("current").asInt(), oNode.path("total").asInt()))
  }
}
