package com.kelsos.mbrc.core.networking

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import java.io.IOException
import java.net.Socket
import org.junit.Test

class ActiveConnectionTest {
  private val socket = mockk<Socket>(relaxed = true)

  private fun connection(input: String, maxMessageBytes: Long): ActiveConnection =
    ActiveConnection(socket, input.reader().buffered(), maxMessageBytes)

  @Test
  fun `reads consecutive CRLF-terminated lines`() {
    val connection = connection("hello\r\nworld\n", maxMessageBytes = 1024)
    assertThat(connection.readLine()).isEqualTo("hello")
    assertThat(connection.readLine()).isEqualTo("world")
  }

  @Test
  fun `returns empty string at end of stream`() {
    val connection = connection("", maxMessageBytes = 1024)
    assertThat(connection.readLine()).isEmpty()
  }

  @Test
  fun `accepts a line within the cap`() {
    val line = "a".repeat(500)
    val connection = connection("$line\r\n", maxMessageBytes = 1024)
    assertThat(connection.readLine()).isEqualTo(line)
  }

  @Test
  fun `rejects an unterminated line that exceeds the cap instead of buffering forever`() {
    // No line terminator: the unbounded readLine would grow until the heap is exhausted.
    val connection = connection("a".repeat(5000), maxMessageBytes = 100)
    val thrown = runCatching { connection.readLine() }.exceptionOrNull()
    assertThat(thrown).isInstanceOf(IOException::class.java)
  }
}
