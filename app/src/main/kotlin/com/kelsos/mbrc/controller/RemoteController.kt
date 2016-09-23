package com.kelsos.mbrc.controller

import android.app.Application
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class RemoteController
@Inject
constructor(bus: RxBus, app: Application) : Runnable {
  private val scope: Scope
  private var commandMap: MutableMap<String, Class<out ICommand>>? = null
  private val eventQueue: LinkedBlockingQueue<IEvent>

  init {
    eventQueue = LinkedBlockingQueue<IEvent>()
    bus.register<MessageEvent>(this, MessageEvent::class.java, { this.handleUserActionEvents(it) })
    scope = Toothpick.openScope(app)
  }

  fun register(type: String, command: Class<out ICommand>) {
    if (commandMap == null) {
      commandMap = HashMap<String, Class<out ICommand>>()
    }
    if (!commandMap!!.containsKey(type)) {
      commandMap!!.put(type, command)
    }
  }

  fun unregister(type: String, command: Class<out ICommand>) {
    if (commandMap!!.containsKey(type) && commandMap!![type] == command) {
      commandMap!!.remove(type)
    }
  }

  /**
   * Takes a MessageEvent and passes it to the command execution function.

   * @param event The message received.
   */

  internal fun handleUserActionEvents(event: MessageEvent) {
    eventQueue.add(event)
  }

  @Suppress("UNCHECKED_CAST")
  @Synchronized internal fun executeCommand(event: IEvent) {
    val commandClass = commandMap!![event.type] ?: return
    val commandInstance: ICommand?
    try {
      commandInstance = scope.getInstance<ICommand>(commandClass as Class<ICommand>)
      if (commandInstance == null) {
        return
      }
      commandInstance.execute(event)
    } catch (ex: Exception) {
      Timber.d(ex, "executing command for type: \t%s", event.type)
      Timber.d("command data: \t%s", event.data)
    }

  }

  override fun run() {
    try {
      //noinspection InfiniteLoopStatement
      while (true) {
        executeCommand(eventQueue.take())
      }
    } catch (e: InterruptedException) {
      Timber.d(e, "Failed to execute command")
    }

  }
}
