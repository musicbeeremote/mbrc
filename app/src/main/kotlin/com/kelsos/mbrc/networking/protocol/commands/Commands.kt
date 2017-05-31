package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import android.content.Intent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.MulticastConfigurationDiscovery
import com.kelsos.mbrc.networking.SocketAction
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.SocketClient
import com.kelsos.mbrc.networking.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionStatusModel
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.platform.RemoteService
import com.kelsos.mbrc.platform.media_session.SessionNotificationManager
import com.kelsos.mbrc.preferences.SettingsManager
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.io.IOException
import java.net.URL
import javax.inject.Inject

class CancelNotificationCommand
@Inject constructor(private val sessionNotificationManager: SessionNotificationManager) : ICommand {

  override fun execute(e: IEvent) {
    sessionNotificationManager.cancelNotification(
      SessionNotificationManager.NOW_PLAYING_PLACEHOLDER
    )
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
@Inject
constructor(
  private val model: MainDataModel,
  private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    val volume: Int = if (model.volume <= 90) {
      val mod = model.volume % 10

      when {
        mod == 0 -> model.volume + 10
        mod < 5 -> model.volume + (10 - mod)
        else -> model.volume + (20 - mod)
      }
    } else {
      100
    }

    bus.post(MessageEvent.action(UserAction(Protocol.PlayerVolume, volume)))
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

class VersionCheckCommand
@Inject
constructor(
  private val model: MainDataModel,
  private val mapper: ObjectMapper,
  private val manager: SettingsManager,
  private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    val now = Instant.now()

    if (check(MINIMUM_REQUIRED)) {
      val next = getNextCheck(true)
      if (next.isAfter(now)) {
        Timber.d("Next update required check is @ $next")
        return
      }
      bus.post(MessageEvent(ProtocolEventType.PluginUpdateRequired))
      model.minimumRequired = MINIMUM_REQUIRED
      model.pluginUpdateRequired = true
      manager.setLastUpdated(now, true)
      return
    }

    if (!manager.isPluginUpdateCheckEnabled()) {
      return
    }

    val nextCheck = getNextCheck()

    if (nextCheck.isAfter(now)) {
      Timber.d("Next update check after @ $nextCheck")
      return
    }

    val jsonNode: JsonNode
    try {
      jsonNode = mapper.readValue(URL(CHECK_URL), JsonNode::class.java)
    } catch (e1: IOException) {
      Timber.d(e1, "While reading json node")
      return
    }

    val expected = jsonNode.path("tag_name").asText().replace("v", "")

    val found = model.pluginVersion
    if (expected != found && check(expected)) {
      model.pluginUpdateAvailable = true
      bus.post(MessageEvent(ProtocolEventType.PluginUpdateAvailable))
    }

    manager.setLastUpdated(now, false)
    Timber.d("Checked for plugin update @ $now. Found: $found expected: $expected")
  }

  private fun getNextCheck(required: Boolean = false): Instant {
    val lastUpdated = manager.getLastUpdated(required)
    val days = if (required) 1L else 2L
    return lastUpdated.plus(days, ChronoUnit.DAYS)
  }

  private fun check(suggestedVersion: String): Boolean {
    val currentVersion = model.pluginVersion.toVersionArray()
    val latestVersion = suggestedVersion.toVersionArray()

    var i = 0
    val currentSize = currentVersion.size
    val latestSize = latestVersion.size
    while (i < currentSize && i < latestSize && currentVersion[i] == latestVersion[i]) {
      i++
    }

    if (i < currentSize && i < latestSize) {
      val diff = currentVersion[i].compareTo(latestVersion[i])
      return diff < 0
    }

    return false
  }

  companion object {
    private const val CHECK_URL =
      "https://api.github.com/repos/musicbeeremote/plugin/releases/latest"
    private const val MINIMUM_REQUIRED = "1.4.0"
  }
}

fun String.toVersionArray(): Array<Int> = split("\\.".toRegex())
  .dropLastWhile(String::isEmpty)
  .take(3)
  .map { it.toInt() }
  .toTypedArray()

class TerminateServiceCommand
@Inject constructor(
  private val application: Application
) : ICommand {

  override fun execute(e: IEvent) {
    if (RemoteService.SERVICE_STOPPING) {
      return
    }
    application.run {
      stopService(Intent(this, RemoteService::class.java))
    }
  }
}
