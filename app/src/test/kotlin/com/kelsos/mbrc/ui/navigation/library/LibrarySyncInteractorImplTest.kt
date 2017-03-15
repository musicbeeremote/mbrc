package com.kelsos.mbrc.ui.navigation.library

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.any
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.TrackRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import toothpick.testing.ToothPickTestModule
import java.util.concurrent.TimeUnit

class LibrarySyncInteractorImplTest {
  @Rule fun toothPickRule() = ToothPickRule(this)

  val TEST_CASE_SCOPE: Class<*> = TestCase::class.java

  @Mock lateinit var genreRepository: GenreRepository
  @Mock lateinit var artistRepository: ArtistRepository
  @Mock lateinit var albumRepository: AlbumRepository
  @Mock lateinit var trackRepository: TrackRepository
  @Mock lateinit var playlistRepository: PlaylistRepository
  @Mock lateinit var bus: RxBus

  private lateinit var ioScheduler: TestScheduler
  private lateinit var mainScheduler: TestScheduler
  private lateinit var scope: Scope

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    ioScheduler = TestScheduler()
    mainScheduler = TestScheduler()
    scope = Toothpick.openScope(TEST_CASE_SCOPE)
    scope.installModules(ToothPickTestModule(this), object : Module() {
      init {
        bind(Scheduler::class.java).withName("io").toInstance(ioScheduler)
        bind(Scheduler::class.java).withName("main").toInstance(mainScheduler)
        bind(LibrarySyncInteractor::class.java).to(LibrarySyncInteractorImpl::class.java).singletonInScope()
      }
    })
  }

  @After
  fun tearDown() {
    Toothpick.closeScope(TEST_CASE_SCOPE)
    Toothpick.reset()
  }

  @Test
  fun emptyLibraryAutoSync() {
    val onCompleteListener = mock(LibrarySyncInteractor.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(true)
    mockSuccessfulRepositoryResponse()

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync(true)

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, never()).onFailure(any())
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  @Test
  fun nonEmptyLibraryAutoSync() {
    val onCompleteListener = mock(LibrarySyncInteractor.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync(true)

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, never()).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, times(1)).onFailure(any())
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  @Test
  fun nonEmptyLibraryManualSyncTwiceConsecutiveCalled() {
    val onCompleteListener = mock(LibrarySyncInteractor.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync()
    sync.sync()

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, never()).onFailure(any())
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  @Test
  fun nonEmptyLibraryManualSyncAndSecondAfterCompletion() {
    val onCompleteListener = mock(LibrarySyncInteractor.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync()

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, never()).onFailure(any())
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))

    // Reset the mocks and run a second sync
    reset(onCompleteListener)
    reset(bus)

    sync.sync()

    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, never()).onFailure(any())
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  @Test
  fun nonEmptyLibraryManualSyncFailure() {
    val onCompleteListener = mock(LibrarySyncInteractor.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(false)
    mockFailedRepositoryResponse()

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync()
    sync.sync()

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, never()).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, times(1)).onFailure(any(Exception::class.java))
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  @Test
  fun syncWithoutCompletionListener() {
    val sync = scope.getInstance(LibrarySyncInteractor::class.java)

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    sync.sync()

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(bus, times(1)).post(any(LibraryRefreshCompleteEvent::class.java))
  }

  private fun mockCacheState(isEmpty: Boolean) {
    `when`(genreRepository.cacheIsEmpty()).thenReturn(Single.just(isEmpty))
    `when`(artistRepository.cacheIsEmpty()).thenReturn(Single.just(isEmpty))
    `when`(albumRepository.cacheIsEmpty()).thenReturn(Single.just(isEmpty))
    `when`(trackRepository.cacheIsEmpty()).thenReturn(Single.just(isEmpty))
  }

  private fun mockSuccessfulRepositoryResponse() {
    `when`(genreRepository.getRemote()).thenReturn(Completable.complete())
    `when`(artistRepository.getRemote()).thenReturn(Completable.complete())
    `when`(albumRepository.getRemote()).thenReturn(Completable.complete())
    `when`(trackRepository.getRemote()).thenReturn(Completable.complete())
    `when`(playlistRepository.getRemote()).thenReturn(Completable.complete())
  }

  private fun mockFailedRepositoryResponse() {
    `when`(genreRepository.getRemote()).thenReturn(Completable.complete())
    `when`(artistRepository.getRemote()).thenReturn(Completable.error(Exception()))
    `when`(albumRepository.getRemote()).thenReturn(Completable.complete())
    `when`(trackRepository.getRemote()).thenReturn(Completable.complete())
    `when`(playlistRepository.getRemote()).thenReturn(Completable.complete())
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class TestCase
}
