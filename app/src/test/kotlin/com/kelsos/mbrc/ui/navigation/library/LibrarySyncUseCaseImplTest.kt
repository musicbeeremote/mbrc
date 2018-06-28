package com.kelsos.mbrc.ui.navigation.library

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.any
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.LibrarySyncUseCaseImpl
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations





import java.util.concurrent.TimeUnit

class LibrarySyncUseCaseImplTest {
  @Rule
  fun toothPickRule() = ToothPickRule(this)

  private val TEST_CASE_SCOPE: Class<*> = TestCase::class.java

  @Mock
  private lateinit var genreRepository: GenreRepository

  @Mock
  private lateinit var artistRepository: ArtistRepository

  @Mock
  private lateinit var albumRepository: AlbumRepository

  @Mock
  private lateinit var trackRepository: TrackRepository

  @Mock
  private lateinit var playlistRepository: PlaylistRepository


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
        bind(LibrarySyncUseCase::class.java).to(LibrarySyncUseCaseImpl::class.java)
          .singletonInScope()
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
    val onCompleteListener = mock(LibrarySyncUseCase.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

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
  }

  @Test
  fun nonEmptyLibraryAutoSync() {
    val onCompleteListener = mock(LibrarySyncUseCase.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

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
  }

  @Test
  fun nonEmptyLibraryManualSyncTwiceConsecutiveCalled() {
    val onCompleteListener = mock(LibrarySyncUseCase.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

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
  }

  @Test
  fun nonEmptyLibraryManualSyncAndSecondAfterCompletion() {
    val onCompleteListener = mock(LibrarySyncUseCase.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

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

    // Reset the mocks and run a second network
    reset(onCompleteListener)


    sync.sync()

    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
    verify(onCompleteListener, times(1)).onSuccess()
    verify(onCompleteListener, times(1)).onTermination()
    verify(onCompleteListener, never()).onFailure(any())
  }

  @Test
  fun nonEmptyLibraryManualSyncFailure() {
    val onCompleteListener = mock(LibrarySyncUseCase.OnCompleteListener::class.java)
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

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
  }

  @Test
  fun syncWithoutCompletionListener() {
    val sync = scope.getInstance(LibrarySyncUseCase::class.java)

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    sync.sync()

    assertThat(sync.isRunning())
    ioScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isTrue()
    ioScheduler.advanceTimeBy(1200, TimeUnit.MILLISECONDS)
    mainScheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS)
    assertThat(sync.isRunning()).isFalse()
  }

  private fun mockCacheState(isEmpty: Boolean) {
    given(genreRepository.cacheIsEmpty()).willReturn(Single.just(isEmpty))
    given(artistRepository.cacheIsEmpty()).willReturn(Single.just(isEmpty))
    given(albumRepository.cacheIsEmpty()).willReturn(Single.just(isEmpty))
    given(trackRepository.cacheIsEmpty()).willReturn(Single.just(isEmpty))
  }

  private fun mockSuccessfulRepositoryResponse() {
    given(genreRepository.getRemote()).willReturn(Completable.complete())
    given(artistRepository.getRemote()).willReturn(Completable.complete())
    given(albumRepository.getRemote()).willReturn(Completable.complete())
    given(trackRepository.getRemote()).willReturn(Completable.complete())
    given(playlistRepository.getRemote()).willReturn(Completable.complete())
  }

  private fun mockFailedRepositoryResponse() {
    given(genreRepository.getRemote()).willReturn(Completable.complete())
    given(artistRepository.getRemote()).willReturn(Completable.error(Exception()))
    given(albumRepository.getRemote()).willReturn(Completable.complete())
    given(trackRepository.getRemote()).willReturn(Completable.complete())
    given(playlistRepository.getRemote()).willReturn(Completable.complete())
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class TestCase
}