package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import android.content.Intent
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.platform.RemoteService
import timber.log.Timber
import javax.inject.Inject

class ProtocolPingHandle
@Inject constructor(
  private val messageQueue: MessageQueue,
  private var activityChecker: SocketActivityChecker
) : ICommand {

  override fun execute(e: IEvent) {
    activityChecker.ping()
    messageQueue.queue(SocketMessage.create(Protocol.PONG))
  }
}

class ProtocolPongHandle
@Inject constructor() : ICommand {
  override fun execute(e: IEvent) {
    Timber.d(e.data.toString())
  }
}

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
