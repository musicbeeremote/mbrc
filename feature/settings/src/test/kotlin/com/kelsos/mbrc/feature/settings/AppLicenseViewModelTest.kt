package com.kelsos.mbrc.feature.settings

import android.app.Application
import android.content.res.AssetManager
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.test.testDispatchers
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AppLicenseViewModelTest : KoinTest {

  private lateinit var viewModel: AppLicenseViewModel
  private lateinit var application: Application
  private lateinit var assetManager: AssetManager

  private val testLicenseText = """
    |GNU GENERAL PUBLIC LICENSE
    |Version 3, 29 June 2007
  """.trimMargin()

  @Before
  fun setUp() {
    assetManager = mockk(relaxed = true) {
      every { open("LICENSE.txt") } returns ByteArrayInputStream(testLicenseText.toByteArray())
    }
    application = mockk(relaxed = true) {
      every { assets } returns assetManager
    }
    startKoin { modules(listOf(testDispatcherModule)) }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `initial state should be loading`() = runTest(testDispatcher) {
    viewModel = AppLicenseViewModel(application, testDispatchers)

    // The initial value of the StateFlow is Loading
    assertThat(viewModel.uiState.value).isEqualTo(AppLicenseUiState.Loading)
  }

  @Test
  fun `should load license text successfully`() = runTest(testDispatcher) {
    viewModel = AppLicenseViewModel(application, testDispatchers)
    advanceUntilIdle()

    viewModel.uiState.test {
      val state = awaitItem()
      assertThat(state).isInstanceOf(AppLicenseUiState.Success::class.java)
      assertThat((state as AppLicenseUiState.Success).licenseText).isEqualTo(testLicenseText)
    }
  }

  @Test
  fun `should handle error when license file not found`() = runTest(testDispatcher) {
    every { assetManager.open("LICENSE.txt") } throws IOException("File not found")

    viewModel = AppLicenseViewModel(application, testDispatchers)
    advanceUntilIdle()

    viewModel.uiState.test {
      val state = awaitItem()
      assertThat(state).isInstanceOf(AppLicenseUiState.Error::class.java)
      assertThat((state as AppLicenseUiState.Error).message).contains("File not found")
    }
  }

  @Test
  fun `retry should reload license after error`() = runTest(testDispatcher) {
    var callCount = 0
    every { assetManager.open("LICENSE.txt") } answers {
      callCount++
      if (callCount == 1) {
        throw IOException("First attempt failed")
      } else {
        ByteArrayInputStream(testLicenseText.toByteArray())
      }
    }

    viewModel = AppLicenseViewModel(application, testDispatchers)
    advanceUntilIdle()

    viewModel.uiState.test {
      // First should be error
      val errorState = awaitItem()
      assertThat(errorState).isInstanceOf(AppLicenseUiState.Error::class.java)

      // Retry
      viewModel.retry()
      advanceUntilIdle()

      // Should transition to loading then success
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(AppLicenseUiState.Loading)

      val successState = awaitItem()
      assertThat(successState).isInstanceOf(AppLicenseUiState.Success::class.java)
    }
  }

  @Test
  fun `should handle empty license file`() = runTest(testDispatcher) {
    every { assetManager.open("LICENSE.txt") } returns ByteArrayInputStream("".toByteArray())

    viewModel = AppLicenseViewModel(application, testDispatchers)
    advanceUntilIdle()

    viewModel.uiState.test {
      val state = awaitItem()
      assertThat(state).isInstanceOf(AppLicenseUiState.Success::class.java)
      assertThat((state as AppLicenseUiState.Success).licenseText).isEmpty()
    }
  }

  @Test
  fun `should access assets on initialization`() = runTest(testDispatcher) {
    viewModel = AppLicenseViewModel(application, testDispatchers)
    advanceUntilIdle()

    verify { assetManager.open("LICENSE.txt") }
  }
}
