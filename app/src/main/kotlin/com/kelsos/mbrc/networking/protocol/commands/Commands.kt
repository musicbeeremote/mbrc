package com.kelsos.mbrc.networking.protocol.commands

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import timber.log.Timber
import javax.inject.Inject

class ProtocolPingHandle
@Inject constructor(
  private val messageQueue: MessageQueue,
  private var activityChecker: SocketActivityChecker
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    activityChecker.ping()
    messageQueue.queue(SocketMessage.create(Protocol.PONG))
  }
}

class ProtocolPongHandle
@Inject constructor() : ICommand {
  override fun execute(message: ProtocolMessage) {
    Timber.d(message.data.toString())
  }
}