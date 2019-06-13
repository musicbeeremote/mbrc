package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.features.lyrics.LyricsPayload
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.lyrics.LyricsStateImpl
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateLyricsTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var updateLyrics: UpdateLyrics
  private lateinit var lyricsState: LyricsState
  private lateinit var moshi: Moshi
  private lateinit var adapter: JsonAdapter<SocketMessage>

  private fun createMessage(status: Int, lyrics: String = ""): String {
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
    lyricsState = LyricsStateImpl()
    updateLyrics = UpdateLyrics(moshi, lyricsState)
  }

  @Test
  fun `not found lyrics should empty the cache`() {
    val socketMessage = checkNotNull(adapter.fromJson(createMessage(LyricsPayload.NOT_FOUND)))
    val message = MessageEvent(socketMessage.context, socketMessage.data)
    lyricsState.set(listOf("a", "b"))
    updateLyrics.execute(message)
    assertThat(lyricsState.requireValue()).hasSize(0)
  }

  @Test
  fun `lyrics should be translated`() {
    val lyrics = """
      &lt;Lyrics&gt;
      <p>
      &quot;Must&quot; follow this format &apos;&amp;<br>
      that
    """.trimIndent()
    val socketMessage = checkNotNull(adapter.fromJson(createMessage(LyricsPayload.SUCCESS, lyrics)))
    val message = MessageEvent(socketMessage.context, socketMessage.data)
    updateLyrics.execute(message)
    assertThat(lyricsState.requireValue()).hasSize(6)
    assertThat(lyricsState.requireValue()).containsExactly(
      "<Lyrics>",
      "",
      "",
      "\"Must\" follow this format '&",
      "",
      "that"
    )
  }
}
