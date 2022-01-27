package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.features.player.LfmRating
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.rules.CoroutineTestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateLfmRatingTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private lateinit var updateLastRating: UpdateLfmRating
  private lateinit var appState: AppState

  private fun createMessage(rating: String) = object : ProtocolMessage {
    override val type: Protocol
      get() = Protocol.NowPlayingLfmRating
    override val data: Any
      get() = rating
  }

  @Before
  fun setUp() {
    appState = AppState()
    updateLastRating = UpdateLfmRating(appState)
  }

  @Test
  fun `should change lfm rating to loved`() = runTest {
    updateLastRating.execute(createMessage("Love"))
    assertThat(appState.playingTrackRating.first().lfmRating).isEqualTo(LfmRating.Loved)
  }

  @Test
  fun `should change lfm rating to banned`() = runTest {
    updateLastRating.execute(createMessage("Ban"))
    assertThat(appState.playingTrackRating.first().lfmRating).isEqualTo(LfmRating.Banned)
  }

  @Test
  fun `should change lfm rating to normal`() = runTest {
    val state = appState.playingTrackRating.first()
    appState.playingTrackRating.emit(state.copy(lfmRating = LfmRating.Loved))
    updateLastRating.execute(createMessage(""))
    assertThat(appState.playingTrackRating.first().lfmRating).isEqualTo(LfmRating.Normal)
  }
}
