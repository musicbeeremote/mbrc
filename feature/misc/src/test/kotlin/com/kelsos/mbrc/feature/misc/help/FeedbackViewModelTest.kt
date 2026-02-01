package com.kelsos.mbrc.feature.misc.help

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.AppInfo
import com.kelsos.mbrc.core.common.utilities.logging.LogHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.io.FileNotFoundException
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
class FeedbackViewModelTest : KoinTest {

  private lateinit var viewModel: FeedbackViewModel
  private lateinit var logHelper: LogHelper
  private lateinit var appInfo: AppInfo

  @Before
  fun setUp() {
    logHelper = mockk(relaxed = true)
    appInfo = mockk {
      every { versionName } returns "1.2.3"
      every { applicationId } returns "com.kelsos.mbrc"
    }

    startKoin { modules(testDispatcherModule) }

    viewModel = FeedbackViewModel(logHelper, appInfo)
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  // region Property tests

  @Test
  fun `versionName should return appInfo versionName`() {
    assertThat(viewModel.versionName).isEqualTo("1.2.3")
  }

  @Test
  fun `applicationId should return appInfo applicationId`() {
    assertThat(viewModel.applicationId).isEqualTo("com.kelsos.mbrc")
  }

  // endregion

  // region checkIfLogsExist tests

  @Test
  fun `checkIfLogsExist should emit UpdateLogsExist true when logs exist`() =
    runTest(testDispatcher) {
      val filesDir = mockk<File>()
      coEvery { logHelper.logsExist(filesDir) } returns true

      viewModel.events.test {
        viewModel.checkIfLogsExist(filesDir)
        advanceUntilIdle()

        val message = awaitItem()
        assertThat(message).isInstanceOf(FeedbackUiMessage.UpdateLogsExist::class.java)
        assertThat((message as FeedbackUiMessage.UpdateLogsExist).logsExist).isTrue()
      }

      coVerify { logHelper.logsExist(filesDir) }
    }

  @Test
  fun `checkIfLogsExist should emit UpdateLogsExist false when logs do not exist`() =
    runTest(testDispatcher) {
      val filesDir = mockk<File>()
      coEvery { logHelper.logsExist(filesDir) } returns false

      viewModel.events.test {
        viewModel.checkIfLogsExist(filesDir)
        advanceUntilIdle()

        val message = awaitItem()
        assertThat(message).isInstanceOf(FeedbackUiMessage.UpdateLogsExist::class.java)
        assertThat((message as FeedbackUiMessage.UpdateLogsExist).logsExist).isFalse()
      }
    }

  @Test
  fun `checkIfLogsExist should call logHelper with correct filesDir`() = runTest(testDispatcher) {
    val filesDir = mockk<File>()
    coEvery { logHelper.logsExist(any()) } returns true

    viewModel.checkIfLogsExist(filesDir)
    advanceUntilIdle()

    coVerify { logHelper.logsExist(filesDir) }
  }

  // endregion

  // region createZip tests

  @Test
  fun `createZip should emit ZipSuccess when zipLogs succeeds`() = runTest(testDispatcher) {
    val filesDir = mockk<File>()
    val cacheDir = mockk<File>()
    val zipFile = mockk<File>()
    coEvery { logHelper.zipLogs(filesDir, cacheDir) } returns zipFile

    viewModel.events.test {
      viewModel.createZip(filesDir, cacheDir)
      advanceUntilIdle()

      val message = awaitItem()
      assertThat(message).isInstanceOf(FeedbackUiMessage.ZipSuccess::class.java)
      assertThat((message as FeedbackUiMessage.ZipSuccess).zipFile).isEqualTo(zipFile)
    }

    coVerify { logHelper.zipLogs(filesDir, cacheDir) }
  }

  @Test
  fun `createZip should emit ZipFailed when zipLogs throws exception`() = runTest(testDispatcher) {
    val filesDir = mockk<File>()
    val cacheDir = mockk<File>()
    coEvery { logHelper.zipLogs(filesDir, cacheDir) } throws FileNotFoundException("No logs found")

    viewModel.events.test {
      viewModel.createZip(filesDir, cacheDir)
      advanceUntilIdle()

      val message = awaitItem()
      assertThat(message).isInstanceOf(FeedbackUiMessage.ZipFailed::class.java)
    }
  }

  @Test
  fun `createZip should emit ZipFailed when zipLogs throws SecurityException`() =
    runTest(testDispatcher) {
      val filesDir = mockk<File>()
      val cacheDir = mockk<File>()
      coEvery { logHelper.zipLogs(filesDir, cacheDir) } throws
        SecurityException("Permission denied")

      viewModel.events.test {
        viewModel.createZip(filesDir, cacheDir)
        advanceUntilIdle()

        val message = awaitItem()
        assertThat(message).isInstanceOf(FeedbackUiMessage.ZipFailed::class.java)
      }
    }

  @Test
  fun `createZip should call logHelper with correct arguments`() = runTest(testDispatcher) {
    val filesDir = mockk<File>()
    val cacheDir = mockk<File>()
    val zipFile = mockk<File>()
    coEvery { logHelper.zipLogs(any(), any()) } returns zipFile

    viewModel.createZip(filesDir, cacheDir)
    advanceUntilIdle()

    coVerify { logHelper.zipLogs(filesDir, cacheDir) }
  }

  // endregion

  // region Integration tests

  @Test
  fun `checkIfLogsExist and createZip can be called sequentially`() = runTest(testDispatcher) {
    val filesDir = mockk<File>()
    val cacheDir = mockk<File>()
    val zipFile = mockk<File>()
    coEvery { logHelper.logsExist(filesDir) } returns true
    coEvery { logHelper.zipLogs(filesDir, cacheDir) } returns zipFile

    viewModel.events.test {
      viewModel.checkIfLogsExist(filesDir)
      advanceUntilIdle()

      val firstMessage = awaitItem()
      assertThat(firstMessage).isInstanceOf(FeedbackUiMessage.UpdateLogsExist::class.java)
      assertThat((firstMessage as FeedbackUiMessage.UpdateLogsExist).logsExist).isTrue()

      viewModel.createZip(filesDir, cacheDir)
      advanceUntilIdle()

      val secondMessage = awaitItem()
      assertThat(secondMessage).isInstanceOf(FeedbackUiMessage.ZipSuccess::class.java)
    }
  }

  // endregion
}
