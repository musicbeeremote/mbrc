package com.kelsos.mbrc.networking.client

import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class MessageQueueImpl
@Inject
constructor() : MessageQueue {

  private lateinit var onMessageAvailable: (message: SocketMessage) -> Unit
  private val messageQueue: LinkedBlockingQueue<SocketMessage> = LinkedBlockingQueue()
  private var running = false

  private var threadPoolExecutor = Executors.newSingleThreadExecutor {
    Thread(it, "message-queue")
  }

  override fun start() {
    if (threadPoolExecutor.isShutdown) {
      threadPoolExecutor = Executors.newSingleThreadExecutor {
        Thread(it, "message-queue")
      }
    }

    threadPoolExecutor.execute(this)
  }

  override fun stop() {
    running = false
    threadPoolExecutor.shutdownNow()
    messageQueue.clear()
  }

  override fun queue(message: SocketMessage) {
    if (!running) {
      return
    }
    messageQueue.put(message)
  }

  override fun setOnMessageAvailable(onMessageAvailable: (message: SocketMessage) -> Unit) {
    this.onMessageAvailable = onMessageAvailable
  }

  override fun run() {
    running = true

    try {
      //noinspection InfiniteLoopStatement
      while (true) {
        onMessageAvailable(messageQueue.take())
      }
    } catch (e: InterruptedException) {
      Timber.d(e, "Failed to execute command")
    }
  }
}
