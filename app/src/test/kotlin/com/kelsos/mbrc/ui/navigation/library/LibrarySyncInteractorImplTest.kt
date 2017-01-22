package com.kelsos.mbrc.ui.navigation.library

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.repository.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import rx.Completable
import rx.Scheduler
import rx.Single
import rx.schedulers.TestScheduler
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

    `when`(genreRepository.cacheIsEmpty()).thenReturn(Single.just(true))
    `when`(artistRepository.cacheIsEmpty()).thenReturn(Single.just(true))
    `when`(albumRepository.cacheIsEmpty()).thenReturn(Single.just(true))
    `when`(trackRepository.cacheIsEmpty()).thenReturn(Single.just(true))

    `when`(genreRepository.getRemote()).thenReturn(Completable.complete())
    `when`(artistRepository.getRemote()).thenReturn(Completable.complete())
    `when`(albumRepository.getRemote()).thenReturn(Completable.complete())
    `when`(trackRepository.getRemote()).thenReturn(Completable.complete())
    `when`(playlistRepository.getRemote()).thenReturn(Completable.complete())

    sync.setOnCompleteListener(onCompleteListener)
    sync.sync(true)

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    // verify one event fired
    // verify no failure called
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class TestCase
}
