package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.library.LibraryService
import com.kelsos.mbrc.library.LibraryServiceImpl
import com.kelsos.mbrc.library.albums.AlbumRepository
import com.kelsos.mbrc.library.albums.AlbumRepositoryImpl
import com.kelsos.mbrc.library.artists.ArtistRepository
import com.kelsos.mbrc.library.artists.ArtistRepositoryImpl
import com.kelsos.mbrc.library.artists.LocalArtistDataSource
import com.kelsos.mbrc.library.artists.LocalArtistDataSourceImpl
import com.kelsos.mbrc.library.genres.GenreRepository
import com.kelsos.mbrc.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.library.tracks.TrackRepository
import com.kelsos.mbrc.library.tracks.TrackRepositoryImpl
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.repository.ModelCache
import com.kelsos.mbrc.repository.ModelCacheImpl
import com.kelsos.mbrc.repository.NowPlayingRepository
import com.kelsos.mbrc.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.repository.RadioRepository
import com.kelsos.mbrc.repository.RadioRepositoryImpl
import com.kelsos.mbrc.repository.data.LocalRadioDataSource
import com.kelsos.mbrc.repository.data.LocalRadioDataSourceImpl
import com.kelsos.mbrc.repository.data.RemoteRadioDataSource
import com.kelsos.mbrc.repository.data.RemoteRadioDataSourceImpl
import com.kelsos.mbrc.services.CoverService
import com.kelsos.mbrc.services.CoverServiceImpl
import com.kelsos.mbrc.services.NowPlayingService
import com.kelsos.mbrc.services.NowPlayingServiceImpl
import com.kelsos.mbrc.services.PlaylistService
import com.kelsos.mbrc.services.PlaylistServiceImpl
import com.kelsos.mbrc.services.QueueService
import com.kelsos.mbrc.services.QueueServiceImpl
import com.kelsos.mbrc.services.RadioService
import com.kelsos.mbrc.services.RadioServiceImpl
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.services.ServiceCheckerImpl
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractorImpl
import com.kelsos.mbrc.utilities.SettingsManager
import com.kelsos.mbrc.utilities.SettingsManagerImpl
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import toothpick.config.Binding
import toothpick.config.Module
import kotlin.reflect.KClass

class RemoteModule : Module() {
  init {
    val mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())

    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()
    bind(ObjectMapper::class.java).toInstance(mapper)
    bind(LibraryService::class.java).to(LibraryServiceImpl::class.java).singletonInScope()
    bind(PlaylistService::class.java).to(PlaylistServiceImpl::class.java).singletonInScope()
    bind(NowPlayingService::class.java).to(NowPlayingServiceImpl::class.java).singletonInScope()
    bind(CoverService::class.java).to(CoverServiceImpl::class.java).singletonInScope()
    bind(QueueService::class.java).to(QueueServiceImpl::class.java).singletonInScope()

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java)
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
    bind(Scheduler::class.java).withName("main").toProviderInstance { AndroidSchedulers.mainThread() }
    bind(Scheduler::class.java).withName("io").toProviderInstance { Schedulers.io() }

    bind(TrackRepository::class.java).to(TrackRepositoryImpl::class.java)
    bind(AlbumRepository::class.java).to(AlbumRepositoryImpl::class.java)
    bind(ArtistRepository::class.java).to(ArtistRepositoryImpl::class.java)
    bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)

    bind(LocalArtistDataSource::class.java).to(LocalArtistDataSourceImpl::class.java)

    bind(NowPlayingRepository::class.java).to(NowPlayingRepositoryImpl::class.java)
    bind(PlaylistRepository::class.java).to(PlaylistRepositoryImpl::class.java)

    bind(SettingsManager::class.java).to(SettingsManagerImpl::class.java).singletonInScope()
    bind(ModelCache::class.java).to(ModelCacheImpl::class.java).singletonInScope()
    bind(ServiceChecker::class.java).to(ServiceCheckerImpl::class.java).singletonInScope()

    bind(LibrarySyncInteractor::class.java).to(LibrarySyncInteractorImpl::class.java).singletonInScope()

    bind(LocalRadioDataSource::class.java).to(LocalRadioDataSourceImpl::class.java).singletonInScope()
    bind(RemoteRadioDataSource::class.java).to(RemoteRadioDataSourceImpl::class.java).singletonInScope()
    bind(RadioRepository::class.java).to(RadioRepositoryImpl::class.java).singletonInScope()
    bind(RadioService::class.java).to(RadioServiceImpl::class.java).singletonInScope()
  }
}

fun <T : Any> Module.bind(clazz: KClass<T>): Binding<T> = bind(clazz.java)

fun <T: Any, K : T> Binding<T>.to(clazz: KClass<K>): Binding<T>.BoundStateForClassBinding = this.to(clazz.java)
