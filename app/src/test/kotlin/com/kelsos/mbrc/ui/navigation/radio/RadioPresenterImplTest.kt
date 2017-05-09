package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.any
import com.kelsos.mbrc.data.QueueResponse
import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.repository.RadioRepository
import com.kelsos.mbrc.rules.MockitoInitializerRule
import com.kelsos.mbrc.services.QueueService
import com.kelsos.mbrc.ui.navigation.radio.RadioActivity.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.net.SocketTimeoutException

class RadioPresenterImplTest {

  val toothpickRule: ToothPickRule = ToothPickRule(this, Presenter::class.java)
      .setRootRegistryPackage("com.kelsos.mbrc")
  @Rule @JvmField val chain: TestRule = RuleChain.outerRule(toothpickRule)
      .around(MockitoInitializerRule(this))

  @Mock lateinit var radioView: RadioView
  @Mock lateinit var radioRepository: RadioRepository
  @Mock lateinit var queueService: QueueService
  @Mock lateinit var result: FlowCursorList<RadioStation>

  private lateinit var presenter: RadioPresenter

  @Before
  fun setUp() {
    toothpickRule.scope.installModules(RadioModule(), TestModule())
    presenter = toothpickRule.getInstance(RadioPresenter::class.java)
  }

  @Test
  fun loadRadios_cacheEmpty_ViewNotAttached() {
    val data = Single.just(result)
    `when`(radioRepository.cacheIsEmpty()).thenReturn(Single.just(true))
    `when`(radioRepository.getAndSaveRemote()).thenReturn(data)

    presenter.load()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, never()).error(any())
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun loadRadios_cacheEmpty_ViewAttached() {
    val data = Single.just(result)
    `when`(radioRepository.cacheIsEmpty()).thenReturn(Single.just(true))
    `when`(radioRepository.getAndSaveRemote()).thenReturn(data)

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, times(1)).update(result)
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadios_cacheNotEmpty_viewAttached() {
    val data = Single.just(result)
    `when`(radioRepository.cacheIsEmpty()).thenReturn(Single.just(false))
    `when`(radioRepository.getAllCursor()).thenReturn(data)

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAllCursor()
    verify(radioView, times(1)).update(result)
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadios_loadError_ViewAttached() {
    val exception = RuntimeException()
    `when`(radioRepository.cacheIsEmpty()).thenReturn(Single.just(false))
    `when`(radioRepository.getAllCursor()).thenReturn(Single.error(exception))

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, times(1)).error(exception)
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadios_loadError_ViewNotAttached() {
    val exception = RuntimeException()
    `when`(radioRepository.cacheIsEmpty()).thenReturn(Single.just(false))
    `when`(radioRepository.getAllCursor()).thenReturn(Single.error(exception))

    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, never()).error(exception)
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }


  @Test
  fun loadRadios_refresh_ViewAttached() {
    val data = Single.just(result)
    `when`(radioRepository.getAndSaveRemote()).thenReturn(data)

    presenter.attach(radioView)
    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, times(1)).update(result)
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadios_refresh_ViewNotAttached() {
    val data = Single.just(result)
    `when`(radioRepository.getAndSaveRemote()).thenReturn(data)

    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, never()).error(any())
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun loadRadios_refreshError_ViewAttached() {
    val error = RuntimeException()
    `when`(radioRepository.getAndSaveRemote()).thenReturn(Single.error(error))

    presenter.attach(radioView)
    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, times(1)).error(error)
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadios_refreshError_ViewNotAttached() {
    val error = RuntimeException()
    `when`(radioRepository.getAndSaveRemote()).thenReturn(Single.error(error))

    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAllCursor()
    verify(radioView, never()).update(result)
    verify(radioView, never()).error(error)
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun playRadio_successful_ViewAttached() {
    val path = "http://fake.rad"
    val queueResponse = QueueResponse(200)
    `when`(queueService.queue(Queue.NOW, listOf(path))).thenReturn(Single.just(queueResponse))

    presenter.attach(radioView)
    presenter.play(path)
    verify(queueService, times(1)).queue(Queue.NOW, listOf(path))
    verify(radioView, never()).radioPlayFailed(any())
    verify(radioView, times(1)).radioPlaySuccessful()
  }

  @Test
  fun playRadio_successful_ViewNotAttached() {
    val path = "http://fake.rad"
    val queueResponse = QueueResponse(200)
    `when`(queueService.queue(Queue.NOW, listOf(path))).thenReturn(Single.just(queueResponse))

    presenter.play(path)
    verify(queueService, times(1)).queue(Queue.NOW, listOf(path))
    verify(radioView, never()).radioPlayFailed(any())
    verify(radioView, never()).radioPlaySuccessful()
  }

  @Test
  fun playRadio_failure_ViewAttached() {
    val path = "http://fake.rad"
    val timeout = SocketTimeoutException()
    `when`(queueService.queue(Queue.NOW, listOf(path))).thenReturn(Single.error(timeout))

    presenter.attach(radioView)
    presenter.play(path)
    verify(queueService, times(1)).queue(Queue.NOW, listOf(path))
    verify(radioView, times(1)).radioPlayFailed(timeout)
    verify(radioView, never()).radioPlaySuccessful()
  }

  @Test
  fun playRadio_failure_ViewNotAttached() {
    val path = "http://fake.rad"
    val timeout = SocketTimeoutException()
    `when`(queueService.queue(Queue.NOW, listOf(path))).thenReturn(Single.error(timeout))

    presenter.play(path)
    verify(queueService, times(1)).queue(Queue.NOW, listOf(path))
    verify(radioView, never()).radioPlayFailed(timeout)
    verify(radioView, never()).radioPlaySuccessful()
  }

  inner class TestModule : Module() {
    init {
      bind(Scheduler::class.java).withName("io").toProviderInstance { Schedulers.trampoline() }
      bind(Scheduler::class.java).withName("main").toProviderInstance { Schedulers.trampoline() }
    }
  }
}
