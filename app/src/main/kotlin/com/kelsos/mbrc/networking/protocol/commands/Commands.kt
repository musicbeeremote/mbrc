package com.kelsos.mbrc.networking.protocol.commands

import com.kelsos.mbrc.annotations.SocketAction
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.MulticastConfigurationDiscovery
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.SocketClient
import com.kelsos.mbrc.networking.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionStatusModel
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.platform.media_session.SessionNotificationManager
import com.kelsos.mbrc.preferences.SettingsManager
import timber.log.Timber
import javax.inject.Inject

class CancelNotificationCommand
@Inject constructor(private val sessionNotificationManager: SessionNotificationManager) : ICommand {

  override fun execute(e: IEvent) {
    sessionNotificationManager.cancelNotification(SessionNotificationManager.NOW_PLAYING_PLACEHOLDER)
  }
}

class InitiateConnectionCommand
@Inject constructor(private val socketClient: SocketClient) : ICommand {

  override fun execute(e: IEvent) {
    socketClient.socketManager(SocketAction.START)
  }
}

class KeyVolumeDownCommand
@Inject constructor(
    private val model: MainDataModel,
    private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    if (model.volume >= 10) {
      val mod = model.volume % 10
      val volume: Int

      if (mod == 0) {
        volume = model.volume - 10
      } else if (mod < 5) {
        volume = model.volume - (10 + mod)
      } else {
        volume = model.volume - mod
      }

      bus.post(MessageEvent.action(UserAction(Protocol.PlayerVolume, volume)))
    }
  }
}

class KeyVolumeUpCommand
@Inject constructor(private val model: MainDataModel, private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    if (model.volume <= 90) {
      val mod = model.volume % 10
      val volume: Int

      if (mod == 0) {
        volume = model.volume + 10
      } else if (mod < 5) {
        volume = model.volume + (10 - mod)
      } else {
        volume = model.volume + (20 - mod)
      }

      bus.post(MessageEvent.action(UserAction(Protocol.PlayerVolume, volume)))
    }
  }
}

class ProcessUserAction
@Inject constructor(private val socket: SocketClient) : ICommand {

  override fun execute(e: IEvent) {
    val action = e.data as UserAction
    socket.sendData(SocketMessage.create(action.context, action.data))
  }
}

class ProtocolPingHandle
@Inject constructor(
    private val client: SocketClient,
    private var activityChecker: SocketActivityChecker
) : ICommand {

  override fun execute(e: IEvent) {
    activityChecker.ping()
    client.sendData(SocketMessage.create(Protocol.PONG))
  }
}

class ProtocolPongHandle
@Inject constructor() : ICommand {
  override fun execute(e: IEvent) {
    Timber.d(e.data.toString())
  }
}

class ProtocolRequest
@Inject constructor(
    private val socket: SocketClient,
    private val settingsManager: SettingsManager
) : ICommand {

  override fun execute(e: IEvent) {
    val payload = ProtocolPayload(settingsManager.getClientId())
    payload.noBroadcast = false
    payload.protocolVersion = Protocol.ProtocolVersionNumber
    socket.sendData(SocketMessage.create(Protocol.ProtocolTag, payload))
  }
}

class ReduceVolumeOnRingCommand
@Inject constructor(
    private val model: MainDataModel,
    private val client: SocketClient
) : ICommand {

  override fun execute(e: IEvent) {
    if (model.isMute || model.volume == 0) {
      return
    }
    client.sendData(SocketMessage.create(Protocol.PlayerVolume, (model.volume * 0.2).toInt()))
  }
}

class RestartConnectionCommand
@Inject constructor(private val socket: SocketClient) : ICommand {

  override fun execute(e: IEvent) {
    socket.socketManager(SocketAction.RESET)
  }
}

class StartDiscoveryCommand
@Inject constructor(private val mDiscovery: MulticastConfigurationDiscovery) : ICommand {

  override fun execute(e: IEvent) {
    mDiscovery.startDiscovery()
  }
}

class TerminateConnectionCommand
@Inject constructor(
    private val client: SocketClient,
    private val statusModel: ConnectionStatusModel
) : ICommand {

  override fun execute(e: IEvent) {
    statusModel.disconnected()
    client.socketManager(SocketAction.TERMINATE)
  }
}

