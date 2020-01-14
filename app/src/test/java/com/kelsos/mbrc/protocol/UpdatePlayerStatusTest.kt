package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusStateImpl
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdatePlayerStatusTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var update: UpdatePlayerStatus
  private lateinit var state: PlayerStatusState
  private lateinit var moshi: Moshi
  private lateinit var adapter: JsonAdapter<SocketMessage>

  private fun createMessage(): String {
    //language=JSON
    return """
      {
        "context": "${Protocol.PlayerStatus}",
        "data": {
          "playermute": true,
          "playerstate": "playing",
          "playerrepeat" : "one",
          "playershuffle": "autodj",
          "scrobbler": true,
          "playervolume":60
        }
      }
      """.trimMargin()
  }

  @Before
  fun setUp() {
    moshi = Moshi.Builder().build()
    state = PlayerStatusStateImpl()
    update = UpdatePlayerStatus(state, moshi)
    adapter = moshi.adapter(SocketMessage::class.java)
  }

  @Test
  fun `should update the player status when the message is processed`() {
    val original = state.requireValue()
    val socketMessage = checkNotNull(adapter.fromJson(createMessage()))
    val message = MessageEvent(socketMessage.context, socketMessage.data)
    update.execute(message)
    val model = state.requireValue()
    assertThat(original).isNotSameAs(model)
    assertThat(model.scrobbling).isTrue()
    assertThat(model.mute).isTrue()
    assertThat(model.volume).isEqualTo(60)
    assertThat(model.shuffle).isEqualTo(ShuffleMode.AUTODJ)
    assertThat(model.state).isEqualTo(PlayerState.PLAYING)
    assertThat(model.repeat).isEqualTo(Repeat.ONE)
  }
}