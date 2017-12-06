package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.content.activestatus.ModelCache
import com.kelsos.mbrc.content.activestatus.ModelCacheImpl
import com.kelsos.mbrc.content.library.LibraryApiImpl
import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.AlbumRepositoryImpl
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepositoryImpl
import com.kelsos.mbrc.content.library.artists.LocalArtistDataSource
import com.kelsos.mbrc.content.library.artists.LocalArtistDataSourceImpl
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.NowPlayingApiImpl
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.NowPlayingService
import com.kelsos.mbrc.content.nowplaying.cover.CoverApi
import com.kelsos.mbrc.content.nowplaying.cover.CoverApiImpl
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.nowplaying.queue.QueueApiImpl
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputApiImpl
import com.kelsos.mbrc.content.playlists.PlaylistApiImpl
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.content.playlists.PlaylistService
import com.kelsos.mbrc.content.radios.*
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.content.sync.LibrarySyncInteractorImpl
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.networking.protocol.VolumeInteractorImpl
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.preferences.*
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.SchedulerProviderImpl
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
    bind(LibraryService::class.java).to(LibraryApiImpl::class.java).singletonInScope()
    bind(PlaylistService::class.java).to(PlaylistApiImpl::class.java).singletonInScope()
    bind(NowPlayingService::class.java).to(NowPlayingApiImpl::class.java).singletonInScope()
    bind(CoverApi::class.java).to(CoverApiImpl::class.java).singletonInScope()
    bind(QueueApi::class.java).to(QueueApiImpl::class.java).singletonInScope()

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java)
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
    bind(Scheduler::class.java).withName("main").toProviderInstance { AndroidSchedulers.mainThread() }
    bind(Scheduler::class.java).withName("io").toProviderInstance { Schedulers.io() }
    bind(SchedulerProvider::class.java).to(SchedulerProviderImpl::class.java).singletonInScope()

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
    bind(RadioApi::class.java).to(RadioApiImpl::class.java).singletonInScope()
    bind(ClientInformationStore::class).to(ClientInformationStoreImpl::class).singletonInScope()
    bind(VolumeInteractor::class).to(VolumeInteractorImpl::class).singletonInScope()
    bind(OutputApi::class).to(OutputApiImpl::class).singletonInScope()
    bind(AlbumSortingStore::class).to(AlbumSortingStoreImpl::class)
  }
}

fun <T : Any> Module.bind(clazz: KClass<T>): Binding<T> = bind(clazz.java)

fun <T : Any, K : T> Binding<T>.to(clazz: KClass<K>): Binding<T>.BoundStateForClassBinding = this.to(clazz.java)
