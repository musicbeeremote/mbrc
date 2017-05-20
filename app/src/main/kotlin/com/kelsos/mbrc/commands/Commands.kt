package com.kelsos.mbrc.commands

import com.kelsos.mbrc.annotations.SocketAction
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.ProtocolPayload
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.messaging.NotificationService
import com.kelsos.mbrc.model.ConnectionModel
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.services.ProtocolHandler
import com.kelsos.mbrc.services.ServiceDiscovery
import com.kelsos.mbrc.services.SocketService
import com.kelsos.mbrc.utilities.SettingsManager
import com.kelsos.mbrc.utilities.SocketActivityChecker
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class CancelNotificationCommand
@Inject constructor(private val notificationService: NotificationService) : ICommand {

  override fun execute(e: IEvent) {
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER)
  }
}

class ConnectionStatusChangedCommand
@Inject constructor(
    private val model: ConnectionModel,
    private val service: SocketService,
    private val notificationService: NotificationService
) : ICommand {

  override fun execute(e: IEvent) {
    model.setConnectionState(e.dataString)

    if (model.isConnectionActive) {
      service.sendData(SocketMessage.create(Protocol.Player, "Android"))
    } else {
      notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER)
    }
  }
}

class HandleHandshake
@Inject constructor(
    private val handler: ProtocolHandler,
    private val model: ConnectionModel
) : ICommand {

  override fun execute(e: IEvent) {
    if (!(e.data as Boolean)) {
      handler.resetHandshake()
      model.setHandShakeDone(false)
    }
  }
}

class InitiateConnectionCommand
@Inject constructor(private val socketService: SocketService) : ICommand {

  override fun execute(e: IEvent) {
    socketService.socketManager(SocketAction.START)
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
@Inject constructor(private val socket: SocketService) : ICommand {

  override fun execute(e: IEvent) {
    val action = e.data as UserAction
    socket.sendData(SocketMessage.create(action.context, action.data))
  }
}

class ProtocolPingHandle
@Inject constructor(
    private val service: SocketService,
    private var activityChecker: SocketActivityChecker
) : ICommand {

  override fun execute(e: IEvent) {
    activityChecker.ping()
    service.sendData(SocketMessage.create(Protocol.PONG))
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
    private val socket: SocketService,
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
    private val service: SocketService
) : ICommand {

  override fun execute(e: IEvent) {
    if (model.isMute || model.volume == 0) {
      return
    }
    service.sendData(SocketMessage.create(Protocol.PlayerVolume, (model.volume * 0.2).toInt()))
  }
}

class RestartConnectionCommand
@Inject constructor(private val socket: SocketService) : ICommand {

  override fun execute(e: IEvent) {
    socket.socketManager(SocketAction.RESET)
  }
}

class SocketDataAvailableCommand
@Inject constructor(private val handler: ProtocolHandler) : ICommand {

  override fun execute(e: IEvent) {
    handler.preProcessIncoming(e.dataString)
        .subscribeOn(Schedulers.io())
        .subscribe({

        }) {
          Timber.d(it, "message processing")
        }
  }
}

class StartDiscoveryCommand
@Inject constructor(private val mDiscovery: ServiceDiscovery) : ICommand {

  override fun execute(e: IEvent) {
    mDiscovery.startDiscovery()
  }
}

class TerminateConnectionCommand
@Inject constructor(
    private val service: SocketService,
    private val model: ConnectionModel
) : ICommand {

  override fun execute(e: IEvent) {
    model.setHandShakeDone(false)
    model.setConnectionState("false")
    service.socketManager(SocketAction.TERMINATE)
  }
}

