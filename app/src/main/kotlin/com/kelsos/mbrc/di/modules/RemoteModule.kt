package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.AlbumRepositoryImpl
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.ArtistRepositoryImpl
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.GenreRepositoryImpl
import com.kelsos.mbrc.repository.NowPlayingRepository
import com.kelsos.mbrc.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.repository.TrackRepository
import com.kelsos.mbrc.repository.TrackRepositoryImpl
import com.kelsos.mbrc.services.CoverService
import com.kelsos.mbrc.services.CoverServiceImpl
import com.kelsos.mbrc.services.LibraryService
import com.kelsos.mbrc.services.LibraryServiceImpl
import com.kelsos.mbrc.services.NowPlayingService
import com.kelsos.mbrc.services.NowPlayingServiceImpl
import com.kelsos.mbrc.services.PlaylistService
import com.kelsos.mbrc.services.PlaylistServiceImpl
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

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java)
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
    bind(Scheduler::class.java).withName("main").toProviderInstance { AndroidSchedulers.mainThread() }
    bind(Scheduler::class.java).withName("io").toProviderInstance { Schedulers.io() }

    bind(TrackRepository::class.java).to(TrackRepositoryImpl::class.java)
    bind(AlbumRepository::class.java).to(AlbumRepositoryImpl::class.java)
    bind(ArtistRepository::class.java).to(ArtistRepositoryImpl::class.java)
    bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)

    bind(NowPlayingRepository::class.java).to(NowPlayingRepositoryImpl::class.java)
    bind(PlaylistRepository::class.java).to(PlaylistRepositoryImpl::class.java)
  }
}
