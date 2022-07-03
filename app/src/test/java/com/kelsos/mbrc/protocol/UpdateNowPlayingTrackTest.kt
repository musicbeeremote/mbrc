package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.widgets.WidgetUpdater
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateNowPlayingTrackTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private val widgetUpdater: WidgetUpdater = mockk()
  private lateinit var update: UpdateNowPlayingTrack
  private lateinit var appState: AppState
  private lateinit var moshi: Moshi

  private lateinit var adapter: JsonAdapter<SocketMessage>

  private fun createMessage(): String {
    //language=JSON
    return """
      {
        "context": "${Protocol.NowPlayingTrack}",
        "data": {
          "artist":  "Gamma Ray",
          "title": "Damn the machine",
          "album": "No world order",
          "year": "2001",
          "path" : "C:\\music\\metal\\Gamma Ray\\No world\\order\\Damn the machine.mp3"
        }
      }
    """.trimMargin()
  }

  @Before
  fun setUp() {
    appState = AppState()
    moshi = Moshi.Builder().build()
    adapter = moshi.adapter(SocketMessage::class.java)
    update = UpdateNowPlayingTrack(
      appState = appState,
      updater = widgetUpdater,
      mapper = moshi,
      cache = mockk(relaxed = true)
    )
  }

  @Test
  fun `it should update the playing track in the state`() = runTest {
    val playingTrack = PlayingTrack()
    appState.playingTrack.emit(playingTrack)
    every { widgetUpdater.updatePlayingTrack(any()) } just Runs

    val value = runCatching { adapter.fromJson(createMessage()) }
    val socketMessage = checkNotNull(value.getOrNull())
    val message = MessageEvent(Protocol.fromString(socketMessage.context), socketMessage.data)
    update.execute(message)

    val track = appState.playingTrack.first()
    assertThat(track.artist).isEqualTo("Gamma Ray")
    verify(exactly = 1) { widgetUpdater.updatePlayingTrack(any()) }
  }
}
