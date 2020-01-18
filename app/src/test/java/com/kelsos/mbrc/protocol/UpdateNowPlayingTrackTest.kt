package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.features.widgets.WidgetUpdater
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateNowPlayingTrackTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private val slot = slot<PlayingTrack>()
  private val widgetUpdater: WidgetUpdater = mockk()
  private lateinit var update: UpdateNowPlayingTrack
  private lateinit var state: PlayingTrackState
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
    state = mockk()
    moshi = Moshi.Builder().build()
    adapter = moshi.adapter(SocketMessage::class.java)
    update = UpdateNowPlayingTrack(
      state = state,
      updater = widgetUpdater,
      mapper = moshi
    )
  }

  @Test
  fun `it should update the playing track in the state`() {
    val playingTrack = PlayingTrack()
    var updatedTrack = playingTrack
    every { state.set(any<PlayingTrack.() -> PlayingTrack>()) } answers {
      firstArg<PlayingTrack.() -> PlayingTrack>().invoke(playingTrack).also {
        updatedTrack = it
      }
    }
    every { widgetUpdater.updatePlayingTrack(any()) } just Runs
    every { state.getValue() } answers { updatedTrack }

    val socketMessage = checkNotNull(adapter.fromJson(createMessage()))
    val message = MessageEvent(socketMessage.context, socketMessage.data)
    update.execute(message)

    assertThat(updatedTrack.artist).isEqualTo("Gamma Ray")
    verify(exactly = 1) { widgetUpdater.updatePlayingTrack(any()) }
  }

  @Test
  fun `it should not try to update the widget if no data exist`() {
    every { state.set(any<PlayingTrack.() -> PlayingTrack>()) } answers {
      firstArg<PlayingTrack.() -> PlayingTrack>().invoke(
        PlayingTrack()
      )
    }
    every { widgetUpdater.updatePlayingTrack(any()) } just Runs
    every { state.getValue() } answers { null }

    val socketMessage = checkNotNull(adapter.fromJson(createMessage()))
    val message = MessageEvent(socketMessage.context, socketMessage.data)
    update.execute(message)

    verify(exactly = 0) { widgetUpdater.updatePlayingTrack(any()) }
  }
}