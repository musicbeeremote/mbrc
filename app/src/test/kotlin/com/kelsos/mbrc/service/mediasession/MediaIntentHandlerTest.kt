package com.kelsos.mbrc.service.mediasession

import android.content.Intent
import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.VolumeModifyUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaIntentHandlerTest {

  private lateinit var mediaIntentHandler: MediaIntentHandler
  private lateinit var userActionUseCase: UserActionUseCase
  private lateinit var volumeModifyUseCase: VolumeModifyUseCase

  @Before
  fun setUp() {
    userActionUseCase = mockk {
      every { tryPerform(any()) } just Runs
    }
    volumeModifyUseCase = mockk {
      coEvery { increase() } just Runs
      coEvery { decrease() } just Runs
    }
    mediaIntentHandler = MediaIntentHandler(userActionUseCase, volumeModifyUseCase)
  }

  // region Null and invalid intent tests

  @Test
  fun `handleMediaIntent should return false for null intent`() {
    val result = mediaIntentHandler.handleMediaIntent(null)
    assertThat(result).isFalse()
  }

  @Test
  fun `handleMediaIntent should return false for non-media-button action`() {
    val intent = Intent("some.other.action")
    val result = mediaIntentHandler.handleMediaIntent(intent)
    assertThat(result).isFalse()
  }

  @Test
  fun `handleMediaIntent should return false for media button with no extras`() {
    val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
    val result = mediaIntentHandler.handleMediaIntent(intent)
    assertThat(result).isFalse()
  }

  @Test
  fun `handleMediaIntent should return false for ACTION_UP key event`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.ACTION_UP)
    val result = mediaIntentHandler.handleMediaIntent(intent)
    assertThat(result).isFalse()
  }

  // endregion

  // region Playback control tests

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_PLAY_PAUSE`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPlayPause)
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_PLAY`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PLAY)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPlay)
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_PAUSE`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PAUSE)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPause)
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_STOP`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_STOP)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerStop)
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_NEXT`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_NEXT)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerNext)
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_MEDIA_PREVIOUS`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPrevious)
  }

  // endregion

  // region Volume control tests

  @Test
  fun `handleMediaIntent should handle KEYCODE_VOLUME_UP`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_VOLUME_UP)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    coVerify { volumeModifyUseCase.increase() }
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_VOLUME_DOWN`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_VOLUME_DOWN)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    coVerify { volumeModifyUseCase.decrease() }
  }

  @Test
  fun `handleMediaIntent should handle KEYCODE_VOLUME_MUTE`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_VOLUME_MUTE)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerMute)
  }

  // endregion

  // region Headset hook / double-click tests

  @Test
  fun `handleMediaIntent should play-pause on single headset hook click`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_HEADSETHOOK)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { userActionUseCase.tryPerform(capture(slot)) }
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPlayPause)
  }

  @Test
  fun `handleMediaIntent should skip to next on double headset hook click`() {
    // First click
    val intent1 = createMediaButtonIntent(KeyEvent.KEYCODE_HEADSETHOOK)
    mediaIntentHandler.handleMediaIntent(intent1)

    // Second click within 350ms (double-click)
    val intent2 = createMediaButtonIntent(KeyEvent.KEYCODE_HEADSETHOOK)
    val result = mediaIntentHandler.handleMediaIntent(intent2)

    assertThat(result).isTrue()

    // Should have called tryPerform twice - once for play/pause, once for next
    val slots = mutableListOf<UserAction>()
    verify(exactly = 2) { userActionUseCase.tryPerform(capture(slots)) }

    // First call should be play/pause
    assertThat(slots[0].protocol).isEqualTo(Protocol.PlayerPlayPause)
    // Second call (double-click) should be next
    assertThat(slots[1].protocol).isEqualTo(Protocol.PlayerNext)
  }

  @Test
  fun `handleMediaIntent should treat slow clicks as separate single clicks`() {
    // Create a fresh handler and mock to test single click in isolation
    val freshUserActionUseCase: UserActionUseCase = mockk {
      every { tryPerform(any()) } just Runs
    }
    val freshHandler = MediaIntentHandler(freshUserActionUseCase, volumeModifyUseCase)

    // Single click on fresh handler (simulating click after timeout from previous)
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_HEADSETHOOK)
    val result = freshHandler.handleMediaIntent(intent)

    assertThat(result).isTrue()
    val slot = slot<UserAction>()
    verify { freshUserActionUseCase.tryPerform(capture(slot)) }
    // Single click should always be play/pause, not next
    assertThat(slot.captured.protocol).isEqualTo(Protocol.PlayerPlayPause)
  }

  // endregion

  // region Unknown key code tests

  @Test
  fun `handleMediaIntent should return false for unknown key code`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_A)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isFalse()
    verify(exactly = 0) { userActionUseCase.tryPerform(any()) }
  }

  @Test
  fun `handleMediaIntent should return false for KEYCODE_CAMERA`() {
    val intent = createMediaButtonIntent(KeyEvent.KEYCODE_CAMERA)

    val result = mediaIntentHandler.handleMediaIntent(intent)

    assertThat(result).isFalse()
  }

  // endregion

  // Helper function
  private fun createMediaButtonIntent(keyCode: Int, action: Int = KeyEvent.ACTION_DOWN): Intent {
    val keyEvent = KeyEvent(action, keyCode)
    return Intent(Intent.ACTION_MEDIA_BUTTON).apply {
      putExtra(Intent.EXTRA_KEY_EVENT, keyEvent)
    }
  }
}
