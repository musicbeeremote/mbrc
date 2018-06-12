package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.DataSource
import com.kelsos.mbrc.any
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.nowplaying.queue.QueueResponse
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.rules.MockitoInitializerRule
import com.kelsos.mbrc.ui.navigation.radio.RadioFragment.Presenter
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.net.SocketTimeoutException

class RadioPresenterImplTest {

  private val toothpickRule: ToothPickRule = ToothPickRule(this, Presenter::class.java)
    .setRootRegistryPackage("com.kelsos.mbrc")
  @Rule
  @JvmField
  val chain: TestRule = RuleChain.outerRule(toothpickRule)
    .around(MockitoInitializerRule(this))

  @Mock
  private lateinit var radioView: RadioView
  @Mock
  private lateinit var radioRepository: RadioRepository
  @Mock
  private lateinit var queueApi: QueueApi
  @Mock
  private lateinit var result: DataSource.Factory<Int, RadioStationEntity>

  private lateinit var presenter: RadioPresenter

  @Before
  fun setUp() {
    toothpickRule.scope.installModules(RadioModule(), TestModule())
    presenter = toothpickRule.getInstance(RadioPresenter::class.java)
  }

  @Test
  fun loadRadiosCacheEmptyViewNotAttached() {
    val data = Single.just(result)

    given(radioRepository.cacheIsEmpty()).willReturn(Single.just(true))
    given(radioRepository.getAndSaveRemote()).willReturn(data)

    presenter.load()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, never()).error(any())
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun loadRadiosCacheEmptyViewAttached() {
    val data = Single.just(result)
    given(radioRepository.cacheIsEmpty()).willReturn(Single.just(true))
    given(radioRepository.getAndSaveRemote()).willReturn(data)

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, times(1)).update(any())
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadiosCacheNotEmptyViewAttached() {
    val data = Single.just(result)
    given(radioRepository.cacheIsEmpty()).willReturn(Single.just(false))
    given(radioRepository.getAll()).willReturn(data)

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAll()
    verify(radioView, times(1)).update(any())
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadiosLoadErrorViewAttached() {
    val exception = RuntimeException()
    given(radioRepository.cacheIsEmpty()).willReturn(Single.just(false))
    given(radioRepository.getAll()).willReturn(Single.error(exception))

    presenter.attach(radioView)
    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, times(1)).error(exception)
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadiosLoadErrorViewNotAttached() {
    val exception = RuntimeException()
    given(radioRepository.cacheIsEmpty()).willReturn(Single.just(false))
    given(radioRepository.getAll()).willReturn(Single.error(exception))

    presenter.load()

    verify(radioRepository, never()).getAndSaveRemote()
    verify(radioRepository, times(1)).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, never()).error(exception)
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun loadRadiosRefreshViewAttached() {
    val data = Single.just(result)
    given(radioRepository.getAndSaveRemote()).willReturn(data)

    presenter.attach(radioView)
    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, times(1)).update(any())
    verify(radioView, never()).error(any())
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadiosRefreshViewNotAttached() {
    val data = Single.just(result)
    given(radioRepository.getAndSaveRemote()).willReturn(data)

    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, never()).error(any())
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun loadRadiosRefreshErrorViewAttached() {
    val error = RuntimeException()
    given(radioRepository.getAndSaveRemote()).willReturn(Single.error(error))

    presenter.attach(radioView)
    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, times(1)).error(error)
    verify(radioView, times(1)).showLoading()
    verify(radioView, times(1)).hideLoading()
  }

  @Test
  fun loadRadiosRefreshErrorViewNotAttached() {
    val error = RuntimeException()
    given(radioRepository.getAndSaveRemote()).willReturn(Single.error(error))

    presenter.refresh()

    verify(radioRepository, times(1)).getAndSaveRemote()
    verify(radioRepository, never()).getAll()
    verify(radioView, never()).update(any())
    verify(radioView, never()).error(error)
    verify(radioView, never()).showLoading()
    verify(radioView, never()).hideLoading()
  }

  @Test
  fun playRadioSuccessfulViewAttached() {
    val path = "http://fake.rad"
    val queueResponse = QueueResponse(200)
    given(queueApi.queue(LibraryPopup.NOW, listOf(path))).willReturn(Single.just(queueResponse))

    presenter.attach(radioView)
    presenter.play(path)
    verify(queueApi, times(1)).queue(LibraryPopup.NOW, listOf(path))
    verify(radioView, never()).radioPlayFailed(any())
    verify(radioView, times(1)).radioPlaySuccessful()
  }

  @Test
  fun playRadioSuccessfulViewNotAttached() {
    val path = "http://fake.rad"
    val queueResponse = QueueResponse(200)
    given(queueApi.queue(LibraryPopup.NOW, listOf(path))).willReturn(Single.just(queueResponse))

    presenter.play(path)
    verify(queueApi, times(1)).queue(LibraryPopup.NOW, listOf(path))
    verify(radioView, never()).radioPlayFailed(any())
    verify(radioView, never()).radioPlaySuccessful()
  }

  @Test
  fun playRadioFailureViewAttached() {
    val path = "http://fake.rad"
    val timeout = SocketTimeoutException()
    given(queueApi.queue(LibraryPopup.NOW, listOf(path))).willReturn(Single.error(timeout))

    presenter.attach(radioView)
    presenter.play(path)
    verify(queueApi, times(1)).queue(LibraryPopup.NOW, listOf(path))
    verify(radioView, times(1)).radioPlayFailed(timeout)
    verify(radioView, never()).radioPlaySuccessful()
  }

  @Test
  fun playRadioFailureViewNotAttached() {
    val path = "http://fake.rad"
    val timeout = SocketTimeoutException()
    given(queueApi.queue(LibraryPopup.NOW, listOf(path))).willReturn(Single.error(timeout))

    presenter.play(path)
    verify(queueApi, times(1)).queue(LibraryPopup.NOW, listOf(path))
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