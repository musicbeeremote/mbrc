package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingStateImpl
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.navigation.player.LfmRating
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class UpdateLfmRatingTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var updateLastRating: UpdateLfmRating
  private lateinit var trackRatingState: TrackRatingState

  private fun createMessage(rating: String) = object : ProtocolMessage {
    override val type: String
      get() = Protocol.NowPlayingLfmRating
    override val data: Any
      get() = rating
  }

  @Before
  fun setUp() {
    trackRatingState = TrackRatingStateImpl()
    updateLastRating = UpdateLfmRating(trackRatingState)
  }

  @Test
  fun `should change lfm rating to loved`() {
    updateLastRating.execute(createMessage("Love"))
    assertThat(trackRatingState.requireValue().lfmRating).isEqualTo(LfmRating.LOVED)
  }

  @Test
  fun `should change lfm rating to banned`() {
    updateLastRating.execute(createMessage("Ban"))
    assertThat(trackRatingState.requireValue().lfmRating).isEqualTo(LfmRating.BANNED)
  }

  @Test
  fun `should change lfm rating to normal`() {
    trackRatingState.set { copy(lfmRating = LfmRating.LOVED) }
    updateLastRating.execute(createMessage(""))
    assertThat(trackRatingState.requireValue().lfmRating).isEqualTo(LfmRating.NORMAL)
  }
}