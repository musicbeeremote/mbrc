package com.kelsos.mbrc.state

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatchers
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayingTrackCacheTest {

  private lateinit var context: Application
  private lateinit var storeFile: File

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    storeFile = File(context.filesDir, "datastore/cache_store.db")
    storeFile.parentFile?.mkdirs()
  }

  @Test
  fun `a corrupted cache file is replaced instead of crashing on every launch`() {
    // Exactly what 1.6.1 crashed on: bytes that are not a valid Store, which the serializer turns
    // into a CorruptionException. Without a corruption handler DataStore rethrows it from every
    // read and write, and because nothing rewrites the file the crash repeats every launch (#343).
    storeFile.writeBytes(byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05))

    val cache = PlayingTrackCacheImpl(context, testDispatchers)

    runTest(testDispatcher) {
      // Startup reads the cache first; it must come back empty rather than throw.
      assertThat(cache.restoreInfo().path).isEmpty()

      // The write path is where this actually crashed: it had no fallback of its own.
      cache.persistInfo(
        BasicTrackInfo(
          artist = "After Forever",
          title = "Live and Learn",
          album = "Mea Culpa",
          path = "/music/after_forever/live_and_learn.mp3"
        )
      )

      // The file is usable again, which is what stops the crash loop from repeating.
      val restored = cache.restoreInfo()
      assertThat(restored.title).isEqualTo("Live and Learn")
      assertThat(restored.artist).isEqualTo("After Forever")
      assertThat(restored.album).isEqualTo("Mea Culpa")
      assertThat(restored.path).isEqualTo("/music/after_forever/live_and_learn.mp3")
    }
  }
}
