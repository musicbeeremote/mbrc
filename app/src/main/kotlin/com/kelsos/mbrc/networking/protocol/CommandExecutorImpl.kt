package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class CommandExecutorImpl
@Inject
constructor(
  private val commandFactory: CommandFactory
) : CommandExecutor {

  private var executor = getExecutor()

  private var commandMap: MutableMap<String, ICommand> = HashMap()
  private val eventQueue: LinkedBlockingQueue<ProtocolMessage> = LinkedBlockingQueue()
  private var running: Boolean = false

  private fun getExecutor(): ExecutorService {
    return Executors.newSingleThreadExecutor {
      Thread(it, "response-queue")
    }
  }

  @Suppress("UNCHECKED_CAST")
  @Synchronized
  private fun executeCommand(event: ProtocolMessage) {
    val context = event.type

    val command = commandMap[context]
    if (command == null) {
      val commandInstance: ICommand
      try {
        commandInstance = commandFactory.create(context)
      } catch (e: Exception) {
        Timber.e(e, "While creating command")
        return
      }
      commandMap[context] = commandInstance
      callExecute(commandInstance, event)
    } else {
      callExecute(command, event)
    }
  }

  private fun callExecute(command: ICommand, event: ProtocolMessage) {
    try {
      command.execute(event)
    } catch (ex: Exception) {
      Timber.d(ex, "executing command for type: ${event.type}")
    }
  }

  override fun start() {
    if (executor.isShutdown) {
      executor = getExecutor()
    }

    executor.execute(this)
  }

  override fun stop() {
    executor.shutdownNow()
    eventQueue.clear()
    commandMap.clear()
  }

  override fun processEvent(event: MessageEvent) {
    if (!running) {
      return
    }
    eventQueue.add(event)
  }

  override fun run() {
    running = true
    try {
      // noinspection InfiniteLoopStatement
      while (true) {
        executeCommand(eventQueue.take())
      }
    } catch (e: InterruptedException) {
      Timber.d(e, "Failed to execute command")
    }
  }
}
