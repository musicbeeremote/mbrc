package com.kelsos.mbrc.feature.misc.whatsnew

import android.app.Application
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.ChangeLogChecker
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class WhatsNewViewModelTest : KoinTest {

  private lateinit var application: Application
  private lateinit var changeLogChecker: ChangeLogChecker
  private lateinit var changelogResourceProvider: ChangelogResourceProvider

  @Before
  fun setUp() {
    application = mockk(relaxed = true)
    changeLogChecker = mockk(relaxed = true)
    changelogResourceProvider = mockk {
      every { changelogResourceId } returns 0
    }

    startKoin { modules(testDispatcherModule) }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  private fun createViewModel() = WhatsNewViewModel(
    application = application,
    changeLogChecker = changeLogChecker,
    changelogResourceProvider = changelogResourceProvider,
    dispatchers = KoinJavaComponent.get(
      com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers::class.java
    )
  )

  // region Initial state tests

  @Test
  fun `showWhatsNew should be false when changeLogChecker returns false`() =
    runTest(testDispatcher) {
      coEvery { changeLogChecker.checkShouldShowChangeLog() } returns false

      val viewModel = createViewModel()
      advanceUntilIdle()

      viewModel.showWhatsNew.test {
        assertThat(awaitItem()).isFalse()
      }
    }

  @Test
  fun `showWhatsNew should be true when changeLogChecker returns true`() = runTest(testDispatcher) {
    coEvery { changeLogChecker.checkShouldShowChangeLog() } returns true

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.showWhatsNew.test {
      assertThat(awaitItem()).isTrue()
    }
  }

  @Test
  fun `isLoading should eventually be false after initialization`() = runTest(testDispatcher) {
    coEvery { changeLogChecker.checkShouldShowChangeLog() } returns false

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.isLoading.test {
      assertThat(awaitItem()).isFalse()
    }
  }

  @Test
  fun `entries should be empty when changeLogChecker returns false`() = runTest(testDispatcher) {
    coEvery { changeLogChecker.checkShouldShowChangeLog() } returns false

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.entries.test {
      assertThat(awaitItem()).isEmpty()
    }
  }

  // endregion

  // region dismiss tests

  @Test
  fun `dismiss should set showWhatsNew to false`() = runTest(testDispatcher) {
    coEvery { changeLogChecker.checkShouldShowChangeLog() } returns true

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.showWhatsNew.test {
      // Initial value should be true
      assertThat(awaitItem()).isTrue()

      // After dismiss
      viewModel.dismiss()
      assertThat(awaitItem()).isFalse()
    }
  }

  @Test
  fun `dismiss should work even if showWhatsNew was already false`() = runTest(testDispatcher) {
    coEvery { changeLogChecker.checkShouldShowChangeLog() } returns false

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.showWhatsNew.test {
      assertThat(awaitItem()).isFalse()

      // Dismiss should not throw
      viewModel.dismiss()

      // Still false after dismiss (no new emission expected since value didn't change)
      expectNoEvents()
    }
  }

  // endregion

  // region Error handling tests

  @Test
  fun `entries should be empty when changeLogChecker throws IOException`() =
    runTest(testDispatcher) {
      coEvery {
        changeLogChecker.checkShouldShowChangeLog()
      } throws java.io.IOException("Network error")

      val viewModel = createViewModel()
      advanceUntilIdle()

      viewModel.entries.test {
        assertThat(awaitItem()).isEmpty()
      }

      // Should not show what's new dialog on error
      viewModel.showWhatsNew.test {
        assertThat(awaitItem()).isFalse()
      }
    }

  // endregion
}
