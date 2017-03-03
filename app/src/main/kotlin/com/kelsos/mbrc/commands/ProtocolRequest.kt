package com.kelsos.mbrc.commands

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.ProtocolPayload
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.services.SocketService
import com.kelsos.mbrc.utilities.SettingsManager
import javax.inject.Inject

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
