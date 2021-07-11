package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.protocol.ProtocolAction
import com.kelsos.mbrc.protocol.ProtocolMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.Executors

class CommandExecutorImpl(
  private val commandFactory: CommandFactory,
  dispatchers: AppCoroutineDispatchers
) : CommandExecutor {
  private val job: Job = SupervisorJob()
  private val scope: CoroutineScope = CoroutineScope(job + dispatchers.io)
  private val queue = MutableSharedFlow<ProtocolMessage>(0, 10)
  private var commands: MutableMap<Protocol, ProtocolAction> = HashMap()
  private val commandDispatcher = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "CommandQueueDispatcher")
  }.asCoroutineDispatcher()
  private var running: Boolean = false
  private var processing: Job? = null

  private suspend fun executeCommand(event: ProtocolMessage) {
    val context = event.type

    val command = commands[context]
    if (command == null) {
      val commandInstance: ProtocolAction
      try {
        commandInstance = commandFactory.create(context)
      } catch (e: Exception) {
        Timber.e(e, "While creating command")
        return
      }
      commands[context] = commandInstance
      callExecute(commandInstance, event)
    } else {
      callExecute(command, event)
    }
  }

  private suspend fun callExecute(command: ProtocolAction, event: ProtocolMessage) {
    try {
      command.execute(event)
    } catch (ex: Exception) {
      Timber.d(ex, "executing command for type: ${event.type}")
      Timber.d(event.toString())
    }
  }

  override fun start() {
    Timber.v("Start: Listening for protocol messages")
    processing = scope.launch(commandDispatcher) {
      running = true
      queue.collect { message ->
        Timber.v("executing message %s", message.type.context)
        executeCommand(message)
      }
    }
  }

  override fun stop() {
    Timber.v("Done: Listening for protocol messages")
    processing?.cancel()
    commands.clear()
    running = false
  }

  override suspend fun queue(event: MessageEvent) {
    queue.emit(event)
  }
}
