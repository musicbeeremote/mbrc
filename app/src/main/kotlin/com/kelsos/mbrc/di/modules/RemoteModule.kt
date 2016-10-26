@file:Suppress("unused")

package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.cache.PlayerStateCacheImpl
import com.kelsos.mbrc.cache.TrackCache
import com.kelsos.mbrc.cache.TrackCacheImpl
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
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor
import com.kelsos.mbrc.interactors.LibraryAlbumInteractorImpl
import com.kelsos.mbrc.interactors.LibrarySyncInteractor
import com.kelsos.mbrc.interactors.LibrarySyncInteractorImpl
import com.kelsos.mbrc.interactors.LibraryTrackInteractor
import com.kelsos.mbrc.interactors.LibraryTrackInteractorImpl
import com.kelsos.mbrc.interactors.MuteInteractor
import com.kelsos.mbrc.interactors.MuteInteractorImpl
import com.kelsos.mbrc.interactors.NowPlayingListInteractor
import com.kelsos.mbrc.interactors.NowPlayingListInteractorImpl
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.interactors.PlayerInteractorImpl
import com.kelsos.mbrc.interactors.PlayerStateInteractor
import com.kelsos.mbrc.interactors.PlayerStateInteractorImpl
import com.kelsos.mbrc.interactors.PlaylistInteractor
import com.kelsos.mbrc.interactors.PlaylistInteractorImpl
import com.kelsos.mbrc.interactors.RepeatInteractor
import com.kelsos.mbrc.interactors.RepeatInteractorImpl
import com.kelsos.mbrc.interactors.ShuffleInteractor
import com.kelsos.mbrc.interactors.ShuffleInteractorImpl
import com.kelsos.mbrc.interactors.TrackCoverInteractor
import com.kelsos.mbrc.interactors.TrackCoverInteractorImpl
import com.kelsos.mbrc.interactors.TrackInfoInteractor
import com.kelsos.mbrc.interactors.TrackInfoInteractorImpl
import com.kelsos.mbrc.interactors.TrackLyricsInteractor
import com.kelsos.mbrc.interactors.TrackLyricsInteractorImpl
import com.kelsos.mbrc.interactors.TrackPositionInteractor
import com.kelsos.mbrc.interactors.TrackPositionInteractorImpl
import com.kelsos.mbrc.interactors.TrackRatingInteractor
import com.kelsos.mbrc.interactors.TrackRatingInteractorImpl
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractorImpl
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor
import com.kelsos.mbrc.interactors.library.GenreArtistInteractorImpl
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractor
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractorImpl
import com.kelsos.mbrc.interactors.playlists.PlaylistAddInteractor
import com.kelsos.mbrc.interactors.playlists.PlaylistAddInteractorImpl
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractor
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractorImpl
import com.kelsos.mbrc.messaging.SocketMessageHandler
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.repository.NowPlayingRepository
import com.kelsos.mbrc.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.repository.TrackRepository
import com.kelsos.mbrc.repository.TrackRepositoryImpl
import com.kelsos.mbrc.repository.library.AlbumRepository
import com.kelsos.mbrc.repository.library.AlbumRepositoryImpl
import com.kelsos.mbrc.repository.library.ArtistRepository
import com.kelsos.mbrc.repository.library.ArtistRepositoryImpl
import com.kelsos.mbrc.repository.library.CoverRepository
import com.kelsos.mbrc.repository.library.CoverRepositoryImpl
import com.kelsos.mbrc.repository.library.GenreRepository
import com.kelsos.mbrc.repository.library.GenreRepositoryImpl
import com.kelsos.mbrc.services.api.ApiService
import com.kelsos.mbrc.services.api.LibraryService
import com.kelsos.mbrc.services.api.NowPlayingService
import com.kelsos.mbrc.services.api.PlayerService
import com.kelsos.mbrc.services.api.PlaylistService
import com.kelsos.mbrc.services.api.TrackService
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.RxBusImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import toothpick.config.Module

class RemoteModule : Module() {
  init {
    bind(ObjectMapper::class.java).toProvider(ObjectMapperProvider::class.java).providesSingletonInScope()
    bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java).providesSingletonInScope()
    bind(Retrofit::class.java).toProvider(RetrofitProvider::class.java).providesSingletonInScope()
    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()

    bind(okhttp3.Interceptor::class.java).toProviderInstance { null }

    bind(TrackInfoInteractor::class.java).to(TrackInfoInteractorImpl::class.java)
    bind(TrackRatingInteractor::class.java).to(TrackRatingInteractorImpl::class.java)
    bind(TrackCoverInteractor::class.java).to(TrackCoverInteractorImpl::class.java)
    bind(TrackLyricsInteractor::class.java).to(TrackLyricsInteractorImpl::class.java)
    bind(TrackPositionInteractor::class.java).to(TrackPositionInteractorImpl::class.java)
    bind(PlayerInteractor::class.java).to(PlayerInteractorImpl::class.java)
    bind(ShuffleInteractor::class.java).to(ShuffleInteractorImpl::class.java)
    bind(RepeatInteractor::class.java).to(RepeatInteractorImpl::class.java)
    bind(PlayerStateInteractor::class.java).to(PlayerStateInteractorImpl::class.java)
    bind(GenreArtistInteractor::class.java).to(GenreArtistInteractorImpl::class.java)
    bind(ArtistAlbumInteractor::class.java).to(ArtistAlbumInteractorImpl::class.java)

    bind(PlaylistAddInteractor::class.java).to(PlaylistAddInteractorImpl::class.java)

    bind(MuteInteractor::class.java).to(MuteInteractorImpl::class.java)
    bind(NowPlayingListInteractor::class.java).to(NowPlayingListInteractorImpl::class.java)
    bind(NowPlayingActionInteractor::class.java).to(NowPlayingActionInteractorImpl::class.java)
    bind(PlaylistInteractor::class.java).to(PlaylistInteractorImpl::class.java)
    bind(LibraryTrackInteractor::class.java).to(LibraryTrackInteractorImpl::class.java)
    bind(PlaylistTrackInteractor::class.java).to(PlaylistTrackInteractorImpl::class.java)

    bind(LibraryAlbumInteractor::class.java).to(LibraryAlbumInteractorImpl::class.java)
    bind(LibrarySyncInteractor::class.java).to(LibrarySyncInteractorImpl::class.java)

    bind(TrackRepository::class.java).to(TrackRepositoryImpl::class.java).singletonInScope()

    bind(ArtistRepository::class.java).to(ArtistRepositoryImpl::class.java).singletonInScope()
    bind(AlbumRepository::class.java).to(AlbumRepositoryImpl::class.java).singletonInScope()
    bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java).singletonInScope()
    bind(com.kelsos.mbrc.repository.library.TrackRepository::class.java).to(com.kelsos.mbrc.repository.library.TrackRepositoryImpl::class.java).singletonInScope()

    bind(CoverRepository::class.java).to(CoverRepositoryImpl::class.java).singletonInScope()

    bind(NowPlayingRepository::class.java).to(NowPlayingRepositoryImpl::class.java).singletonInScope()
    bind(PlaylistRepository::class.java).to(PlaylistRepositoryImpl::class.java).singletonInScope()
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java).singletonInScope()

    bind(TrackCache::class.java).to(TrackCacheImpl::class.java).singletonInScope()
    bind(PlayerStateCache::class.java).to(PlayerStateCacheImpl::class.java).singletonInScope()

    bind(TrackService::class.java).toProvider(TrackServiceProvider::class.java).providesSingletonInScope()
    bind(PlayerService::class.java).toProvider(PlayerServiceProvider::class.java).providesSingletonInScope()
    bind(LibraryService::class.java).toProvider(LibraryServiceProvider::class.java).providesSingletonInScope()
    bind(NowPlayingService::class.java).toProvider(NowPlayingServiceProvider::class.java).providesSingletonInScope()
    bind(PlaylistService::class.java).toProvider(PlaylistServiceProvider::class.java).providesSingletonInScope()
    bind(ApiService::class.java).toProvider(ApiProvider::class.java).providesSingletonInScope()

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java).providesSingletonInScope()
    bind(com.kelsos.mbrc.utilities.KeyProvider::class.java).to(com.kelsos.mbrc.utilities.KeyProviderImpl::class.java)
    bind(SocketMessageHandler::class.java).singletonInScope()
  }
}

