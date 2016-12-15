package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.di.providers.ApiProvider
import com.kelsos.mbrc.di.providers.LibraryServiceProvider
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.di.providers.NowPlayingServiceProvider
import com.kelsos.mbrc.di.providers.ObjectMapperProvider
import com.kelsos.mbrc.di.providers.OkHttpClientProvider
import com.kelsos.mbrc.di.providers.PlayerServiceProvider
import com.kelsos.mbrc.di.providers.PlaylistServiceProvider
import com.kelsos.mbrc.di.providers.RetrofitProvider
import com.kelsos.mbrc.di.providers.TrackServiceProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.messaging.SocketMessageHandler
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.repository.ModelCache
import com.kelsos.mbrc.repository.ModelCacheImpl
import com.kelsos.mbrc.repository.library.AlbumRepository
import com.kelsos.mbrc.repository.library.AlbumRepositoryImpl
import com.kelsos.mbrc.repository.library.ArtistRepository
import com.kelsos.mbrc.repository.library.ArtistRepositoryImpl
import com.kelsos.mbrc.repository.library.CoverRepository
import com.kelsos.mbrc.repository.library.CoverRepositoryImpl
import com.kelsos.mbrc.repository.library.GenreRepository
import com.kelsos.mbrc.repository.library.GenreRepositoryImpl
import com.kelsos.mbrc.repository.library.TrackRepository
import com.kelsos.mbrc.repository.library.TrackRepositoryImpl
import com.kelsos.mbrc.repository.playlists.NowPlayingRepository
import com.kelsos.mbrc.repository.playlists.NowPlayingRepositoryImpl
import com.kelsos.mbrc.repository.playlists.PlaylistRepository
import com.kelsos.mbrc.repository.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.services.api.ApiService
import com.kelsos.mbrc.services.api.LibraryService
import com.kelsos.mbrc.services.api.NowPlayingService
import com.kelsos.mbrc.services.api.PlayerService
import com.kelsos.mbrc.services.api.PlaylistService
import com.kelsos.mbrc.services.api.TrackService
import com.kelsos.mbrc.utilities.SettingsManager
import com.kelsos.mbrc.utilities.SettingsManagerImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import toothpick.config.Module

class RemoteModule : Module() {
  init {
    bind(ObjectMapper::class.java).toProvider(ObjectMapperProvider::class.java).providesSingletonInScope()
    bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java).providesSingletonInScope()
    bind(Retrofit::class.java).toProvider(RetrofitProvider::class.java).providesSingletonInScope()
    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()

    bind(okhttp3.Interceptor::class.java).toProviderInstance { null }

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


    bind(TrackRepository::class.java).to(TrackRepositoryImpl::class.java).singletonInScope()

    bind(ArtistRepository::class.java).to(ArtistRepositoryImpl::class.java).singletonInScope()
    bind(AlbumRepository::class.java).to(AlbumRepositoryImpl::class.java).singletonInScope()
    bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java).singletonInScope()
    bind(com.kelsos.mbrc.repository.library.TrackRepository::class.java).to(TrackRepositoryImpl::class.java).singletonInScope()

    bind(CoverRepository::class.java).to(CoverRepositoryImpl::class.java).singletonInScope()

    bind(NowPlayingRepository::class.java).to(NowPlayingRepositoryImpl::class.java).singletonInScope()
    bind(PlaylistRepository::class.java).to(PlaylistRepositoryImpl::class.java).singletonInScope()
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java).singletonInScope()

    bind(TrackService::class.java).toProvider(TrackServiceProvider::class.java).providesSingletonInScope()
    bind(PlayerService::class.java).toProvider(PlayerServiceProvider::class.java).providesSingletonInScope()
    bind(LibraryService::class.java).toProvider(LibraryServiceProvider::class.java).providesSingletonInScope()
    bind(NowPlayingService::class.java).toProvider(NowPlayingServiceProvider::class.java).providesSingletonInScope()
    bind(PlaylistService::class.java).toProvider(PlaylistServiceProvider::class.java).providesSingletonInScope()
    bind(ApiService::class.java).toProvider(ApiProvider::class.java).providesSingletonInScope()

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java).providesSingletonInScope()
    bind(com.kelsos.mbrc.utilities.KeyProvider::class.java).to(com.kelsos.mbrc.utilities.KeyProviderImpl::class.java)
    bind(SocketMessageHandler::class.java).singletonInScope()

    bind(SettingsManager::class.java).to(SettingsManagerImpl::class.java).singletonInScope()
    bind(ModelCache::class.java).to(ModelCacheImpl::class.java).singletonInScope()
  }
}

