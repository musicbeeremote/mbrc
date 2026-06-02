package com.kelsos.mbrc.core.networking

import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.net.Socket
import timber.log.Timber

class ActiveConnection(
  private val socket: Socket,
  private val bufferedReader: BufferedReader,
  private val maxMessageBytes: Long = defaultMaxMessageBytes()
) : Closeable {
  fun send(bytes: ByteArray) {
    socket.getOutputStream().write(bytes)
  }

  /**
   * Reads a single line, refusing to accumulate more than [maxMessageBytes] characters. The raw
   * [BufferedReader.readLine] is unbounded: a peer that never terminates a line would grow the
   * buffer until the heap is exhausted. Returns an empty string on end of stream (preserving the
   * non-null contract callers rely on).
   */
  fun readLine(): String {
    val builder = StringBuilder()
    while (true) {
      val char = bufferedReader.read()
      if (char == -1 || char == LINE_FEED) {
        break
      }
      if (char != CARRIAGE_RETURN) {
        builder.append(char.toChar())
      }
      if (builder.length >= maxMessageBytes) {
        throw IOException(
          "Inbound response exceeded $maxMessageBytes characters without a line terminator"
        )
      }
    }
    return builder.toString()
  }

  override fun close() {
    socket.cleanup()
  }

  private fun Socket.cleanup() {
    Timber.v("Cleaning auxiliary socket")
    if (!this.isClosed) {
      try {
        this.close()
      } catch (ex: IOException) {
        Timber.v(ex, "Failed to clause the auxiliary socket")
      }
    }
  }

  private companion object {
    private const val LINE_FEED = '\n'.code
    private const val CARRIAGE_RETURN = '\r'.code
  }
}
