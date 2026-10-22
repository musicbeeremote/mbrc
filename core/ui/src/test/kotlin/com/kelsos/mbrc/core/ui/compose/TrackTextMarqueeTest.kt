package com.kelsos.mbrc.core.ui.compose

import android.content.Context
import android.provider.Settings
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Coverage for #328.
 *
 * `basicMarquee` animates under a fixed motion duration scale, so it ignores the system animation
 * setting on its own. With `iterations = Int.MAX_VALUE` that would mean text scrolling forever for
 * someone who has turned animations off, which is what [trackTextMarquee] gates.
 */
@RunWith(RobolectricTestRunner::class)
class TrackTextMarqueeTest {
  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun `no marquee is applied when animations are off`() {
    setAnimatorDurationScale(0f)

    assertThat(marqueeApplied()).isFalse()
  }

  @Test
  fun `a marquee is applied when animations are on`() {
    setAnimatorDurationScale(1f)

    assertThat(marqueeApplied()).isTrue()
  }

  @Test
  fun `a scaled but non zero animation setting still marquees`() {
    setAnimatorDurationScale(0.5f)

    assertThat(marqueeApplied()).isTrue()
  }

  /** True when [trackTextMarquee] added something to the chain rather than returning it unchanged. */
  private fun marqueeApplied(): Boolean {
    var applied = false
    composeRule.setContent { applied = Modifier.trackTextMarquee() != Modifier }
    composeRule.waitForIdle()
    return applied
  }

  private fun setAnimatorDurationScale(scale: Float) {
    val context = ApplicationProvider.getApplicationContext<Context>()
    Settings.Global.putFloat(
      context.contentResolver,
      Settings.Global.ANIMATOR_DURATION_SCALE,
      scale
    )
  }
}
