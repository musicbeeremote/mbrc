package com.kelsos.mbrc.core.networking.connectlink

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReachableHostFinderTest {

  /** Each address answers [Pair.second] after [Pair.first] ms; an unlisted one fails at once. */
  private class ScriptedProbe(private val replies: Map<String, Pair<Long, Boolean>>) : PluginProbe {
    override suspend fun answers(address: String, port: Int): Boolean {
      val (after, answered) = replies[address] ?: return false
      delay(after)
      return answered
    }
  }

  @Test
  fun `the first address in link order wins even when a later one answers sooner`() = runTest {
    val probe = ScriptedProbe(
      mapOf(
        "192.168.1.5" to (500L to true),
        "172.20.48.1" to (10L to true)
      )
    )

    val winner = ReachableHostFinder(
      probe
    ).firstReachable(listOf("192.168.1.5", "172.20.48.1"), 3000)

    assertThat(winner).isEqualTo("192.168.1.5")
  }

  @Test
  fun `an address that times out falls through to the next one`() = runTest {
    val probe = ScriptedProbe(
      mapOf(
        "172.20.48.1" to (SocketPluginProbe.TIMEOUT_MS.toLong() to false),
        "192.168.1.5" to (20L to true)
      )
    )

    val winner = ReachableHostFinder(
      probe
    ).firstReachable(listOf("172.20.48.1", "192.168.1.5"), 3000)

    assertThat(winner).isEqualTo("192.168.1.5")
    assertWithMessage("every address is probed at once, so the wait is one timeout, not their sum")
      .that(currentTime)
      .isEqualTo(SocketPluginProbe.TIMEOUT_MS.toLong())
  }

  @Test
  fun `no answering address gives no winner`() = runTest {
    val probe = ScriptedProbe(mapOf("10.0.0.3" to (100L to false)))

    assertThat(ReachableHostFinder(probe).firstReachable(listOf("10.0.0.3", "10.0.0.4"), 3000))
      .isNull()
  }

  @Test
  fun `the remaining probes are cancelled once the winner is known`() = runTest {
    val probe = object : PluginProbe {
      val cancelled = mutableListOf<String>()

      override suspend fun answers(address: String, port: Int): Boolean {
        if (address == "10.0.0.3") {
          return true
        }
        try {
          awaitCancellation()
        } finally {
          cancelled += address
        }
      }
    }

    val winner = ReachableHostFinder(
      probe
    ).firstReachable(listOf("10.0.0.3", "10.0.0.4", "10.0.0.5"), 3000)

    assertThat(winner).isEqualTo("10.0.0.3")
    assertWithMessage("a probe left open holds one of the plugin's per-IP connection slots")
      .that(probe.cancelled)
      .containsExactly("10.0.0.4", "10.0.0.5")
  }
}
