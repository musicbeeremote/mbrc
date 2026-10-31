package com.kelsos.mbrc.ui

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConnectLinkIntentFilterTest {

  private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

  private fun handlerOf(link: String): String? {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
      .addCategory(Intent.CATEGORY_BROWSABLE)
      .setPackage(context.packageName)
    return context.packageManager.resolveActivity(intent, 0)?.activityInfo?.name
  }

  @Test
  fun `the canonical QR link opens MainActivity`() {
    val link = "https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5,172.20.48.1&p=3000" +
      "&id=3f2b8c1e-9d4a-4b6f-8e2d-1a7c5b9e0f34&n=LIVING-ROOM-PC"

    assertThat(handlerOf(link)).isEqualTo(MainActivity::class.java.name)
  }

  @Test
  fun `the fallback page's query string form opens MainActivity`() {
    assertThat(handlerOf("https://mbrc.kelsos.net/connect?v=1&h=192.168.1.5&p=3000"))
      .isEqualTo(MainActivity::class.java.name)
  }

  @Test
  fun `other pages on the site stay in the browser`() {
    listOf(
      "https://mbrc.kelsos.net/",
      "https://mbrc.kelsos.net/help/1.7/",
      "http://mbrc.kelsos.net/connect#v=1&h=192.168.1.5&p=3000"
    ).forEach { link ->
      assertWithMessage(link).that(handlerOf(link)).isNull()
    }
  }
}
