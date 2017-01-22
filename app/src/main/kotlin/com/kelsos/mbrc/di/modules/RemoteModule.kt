package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.repository.*
import com.kelsos.mbrc.repository.data.LocalArtistDataSource
import com.kelsos.mbrc.repository.data.LocalArtistDataSourceImpl
import com.kelsos.mbrc.services.*
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractorImpl
import com.kelsos.mbrc.utilities.SettingsManager
import com.kelsos.mbrc.utilities.SettingsManagerImpl
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import toothpick.config.Module

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
  }
}
