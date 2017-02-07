package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.helper.QueueResult
import com.kelsos.mbrc.repository.RadioRepository
import com.kelsos.mbrc.ui.navigation.radio.RadioActivity.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import toothpick.config.Module
import toothpick.testing.ToothPickRule

class RadioPresenterImplTest {

  private val toothpickRule: ToothPickRule = ToothPickRule(this, Presenter::class.java)
    .setRootRegistryPackage("com.kelsos.mbrc")

  @Rule
  @JvmField
  val chain: TestRule = RuleChain.outerRule(toothpickRule)
  private val radioView: RadioView = mockk()
  private val radioRepository: RadioRepository = mockk()
  private val queue: QueueHandler = mockk()
  private val result: FlowCursorList<RadioStation> = mockk()
  private val testDispatcher = TestCoroutineDispatcher()

  private lateinit var presenter: RadioPresenter

  @Before
  fun setUp() {
    toothpickRule.scope.installModules(RadioModule(), TestModule())
    presenter = toothpickRule.getInstance(RadioPresenter::class.java)
    every { radioView.showLoading() } just Runs
    every { radioView.hideLoading() } just Runs
    every { radioView.update(any()) } just Runs
    every { radioView.error(any()) } just Runs
  }

  @Test
  fun loadRadios_cacheEmpty_ViewAttached() = testDispatcher.runBlockingTest {
    val data = result
    coEvery { radioRepository.cacheIsEmpty() } returns true
    coEvery { radioRepository.getAndSaveRemote() } returns data

    presenter.attach(radioView)
    presenter.load()

    coVerify(exactly = 1) { radioRepository.getAndSaveRemote() }
    verify(exactly = 1) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(any()) }
    verify(exactly = 1) { radioView.showLoading() }
    verify(exactly = 1) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_cacheNotEmpty_viewAttached() = testDispatcher.runBlockingTest {
    coEvery { radioRepository.cacheIsEmpty() } returns false
    coEvery { radioRepository.getAndSaveRemote() } returns result
    presenter.attach(radioView)
    presenter.load()

    coVerify(exactly = 1) { radioRepository.getAndSaveRemote() }
    verify(exactly = 1) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(any()) }
    verify(exactly = 1) { radioView.showLoading() }
    verify(exactly = 1) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_loadError_ViewAttached() = testDispatcher.runBlockingTest {
    val exception = RuntimeException()
    coEvery { radioRepository.cacheIsEmpty() } returns false
    coEvery { radioRepository.getAndSaveRemote() } throws exception

    presenter.attach(radioView)
    presenter.load()

    coVerify(exactly = 1) { radioRepository.getAndSaveRemote() }
    verify(exactly = 0) { radioView.update(result) }
    verify(exactly = 1) { radioView.error(exception) }
    verify(exactly = 1) { radioView.showLoading() }
    verify(exactly = 1) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_loadError_ViewNotAttached() = testDispatcher.runBlockingTest {
    val exception = RuntimeException()
    coEvery { radioRepository.cacheIsEmpty() } returns false

    presenter.load()

    coVerify(exactly = 0) { radioRepository.getAndSaveRemote() }
    verify(exactly = 0) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(exception) }
    verify(exactly = 0) { radioView.showLoading() }
    verify(exactly = 0) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_refresh_ViewAttached() = testDispatcher.runBlockingTest {
    val data = result
    coEvery { radioRepository.getAndSaveRemote() } returns data

    presenter.attach(radioView)
    presenter.refresh()

    coVerify(exactly = 1) { radioRepository.getAndSaveRemote() }
    verify(exactly = 1) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(any()) }
    verify(exactly = 1) { radioView.showLoading() }
    verify(exactly = 1) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_refresh_ViewNotAttached() = testDispatcher.runBlockingTest {
    val data = result
    coEvery { radioRepository.getAndSaveRemote() } returns data

    presenter.refresh()

    coVerify(exactly = 0) { radioRepository.getAndSaveRemote() }
    verify(exactly = 0) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(any()) }
    verify(exactly = 0) { radioView.showLoading() }
    verify(exactly = 0) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_refreshError_ViewAttached() = testDispatcher.runBlockingTest {
    val error = RuntimeException()
    coEvery { radioRepository.getAndSaveRemote() } throws error

    presenter.attach(radioView)
    presenter.refresh()

    coVerify(exactly = 1) { radioRepository.getAndSaveRemote() }
    verify(exactly = 0) { radioView.update(result) }
    verify(exactly = 1) { radioView.error(error) }
    verify(exactly = 1) { radioView.showLoading() }
    verify(exactly = 1) { radioView.hideLoading() }
  }

  @Test
  fun loadRadios_refreshError_ViewNotAttached() = testDispatcher.runBlockingTest {
    val error = RuntimeException()
    coEvery { radioRepository.getAndSaveRemote() } throws error

    presenter.refresh()

    coVerify(exactly = 0) { radioRepository.getAndSaveRemote() }
    verify(exactly = 0) { radioView.update(result) }
    verify(exactly = 0) { radioView.error(error) }
    verify(exactly = 0) { radioView.showLoading() }
    verify(exactly = 0) { radioView.hideLoading() }
  }

  @Test
  fun playRadio_successful_ViewAttached() = testDispatcher.runBlockingTest {
    val path = "http://fake.rad"
    coEvery { queue.queuePath(path) } returns QueueResult(true, 1)

    presenter.attach(radioView)
    presenter.play(path)
    coVerify(exactly = 1) { queue.queuePath(path) }
    verify(exactly = 0) { radioView.radioPlayFailed() }
    verify(exactly = 1) { radioView.radioPlaySuccessful() }
  }

  @Test
  fun playRadio_successful_ViewNotAttached() = testDispatcher.runBlockingTest {
    val path = "http://fake.rad"
    coEvery { queue.queuePath(path) } returns QueueResult(true, 1)

    presenter.play(path)
    coVerify(exactly = 1) { queue.queuePath(path) }
    verify(exactly = 0) { radioView.radioPlayFailed() }
    verify(exactly = 0) { radioView.radioPlaySuccessful() }
  }

  @Test
  fun playRadio_failure_ViewAttached() = testDispatcher.runBlockingTest {
    val path = "http://fake.rad"
    coEvery { queue.queuePath(path) } returns QueueResult(false, 1)

    presenter.attach(radioView)
    presenter.play(path)
    coVerify(exactly = 1) { queue.queuePath(path) }
    verify(exactly = 1) { radioView.radioPlayFailed() }
    verify(exactly = 0) { radioView.radioPlaySuccessful() }
  }

  @Test
  fun playRadio_failure_ViewNotAttached() = testDispatcher.runBlockingTest {
    val path = "http://fake.rad"
    coEvery { queue.queuePath(path) } returns QueueResult(false, 1)

    presenter.play(path)
    coVerify(exactly = 1) { queue.queuePath(path) }
    verify(exactly = 0) { radioView.radioPlayFailed() }
    verify(exactly = 0) { radioView.radioPlaySuccessful() }
  }

  inner class TestModule : Module() {
    init {
      bind(AppDispatchers::class.java).toInstance(
        AppDispatchers(
          testDispatcher,
          testDispatcher,
          testDispatcher
        )
      )
      bind(RadioView::class.java).toInstance(radioView)
      bind(RadioRepository::class.java).toInstance(radioRepository)
      bind(QueueHandler::class.java).toInstance(queue)
    }
  }
}
