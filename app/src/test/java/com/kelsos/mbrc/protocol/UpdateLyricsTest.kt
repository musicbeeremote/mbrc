package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.features.lyrics.LyricsPayload
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateLyricsTest {
  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private lateinit var updateLyrics: UpdateLyrics
  private lateinit var appState: AppState
  private lateinit var moshi: Moshi
  private lateinit var adapter: JsonAdapter<SocketMessage>

  private fun createMessage(
    status: Int,
    lyrics: String = "",
  ): String {
    //language=JSON
    return """
      {
        "context": "${Protocol.NowPlayingLyrics}",
        "data": {
          "status": "$status",
          "lyrics": "$lyrics"
        }
      }
      """.trimMargin()
  }

  @Before
  fun setUp() {
    moshi = Moshi.Builder().build()
    adapter = moshi.adapter(SocketMessage::class.java)
    appState = AppState()
    updateLyrics = UpdateLyrics(moshi, appState)
  }

  @Test
  fun `not found lyrics should empty the cache`() =
    runTest {
      val value = runCatching { adapter.fromJson(createMessage(LyricsPayload.NOT_FOUND)) }
      val socketMessage = checkNotNull(value.getOrNull())
      val message = MessageEvent(Protocol.fromString(socketMessage.context), socketMessage.data)
      appState.lyrics.emit(listOf("a", "b"))
      updateLyrics.execute(message)
      assertThat(appState.lyrics.first()).hasSize(0)
    }

  @Test
  fun `lyrics should be translated`() =
    runTest {
      val lyrics =
        """
        &lt;Lyrics&gt;
        <p>
        &quot;Must&quot; follow this format &apos;&amp;<br>
        that
        """.trimIndent()
      val value = runCatching { adapter.fromJson(createMessage(LyricsPayload.SUCCESS, lyrics)) }
      val socketMessage = checkNotNull(value.getOrNull())
      val message = MessageEvent(Protocol.fromString(socketMessage.context), socketMessage.data)
      updateLyrics.execute(message)
      val list = appState.lyrics.first()
      assertThat(list).hasSize(6)
      assertThat(list).containsExactly(
        "<Lyrics>",
        "",
        "",
        "\"Must\" follow this format '&",
        "",
        "that",
      )
    }
}
