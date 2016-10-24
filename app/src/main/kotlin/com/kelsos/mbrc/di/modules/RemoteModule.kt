@file:Suppress("unused")

package com.kelsos.mbrc.di.modules

import android.support.v4.app.FragmentManager
import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.cache.PlayerStateCacheImpl
import com.kelsos.mbrc.cache.TrackCache
import com.kelsos.mbrc.cache.TrackCacheImpl
import com.kelsos.mbrc.di.providers.ApiServiceProvider
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.di.providers.ObjectMapperProvider
import com.kelsos.mbrc.di.providers.OkHttpClientProvider
import com.kelsos.mbrc.di.providers.RetrofitProvider
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
import com.kelsos.mbrc.presenters.AlbumTracksPresenter
import com.kelsos.mbrc.presenters.AlbumTracksPresenterImpl
import com.kelsos.mbrc.presenters.ArtistAlbumPresenter
import com.kelsos.mbrc.presenters.ArtistAlbumPresenterImpl
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter
import com.kelsos.mbrc.presenters.BrowseAlbumPresenterImpl
import com.kelsos.mbrc.presenters.BrowseGenrePresenter
import com.kelsos.mbrc.presenters.BrowseGenrePresenterImpl
import com.kelsos.mbrc.presenters.BrowseTrackPresenter
import com.kelsos.mbrc.presenters.BrowseTrackPresenterImpl
import com.kelsos.mbrc.presenters.ConnectionManagerPresenter
import com.kelsos.mbrc.presenters.ConnectionManagerPresenterImpl
import com.kelsos.mbrc.presenters.GenreArtistsPresenter
import com.kelsos.mbrc.presenters.GenreArtistsPresenterImpl
import com.kelsos.mbrc.presenters.LibraryActivityPresenter
import com.kelsos.mbrc.presenters.LibraryActivityPresenterImpl
import com.kelsos.mbrc.presenters.LyricsPresenter
import com.kelsos.mbrc.presenters.LyricsPresenterImpl
import com.kelsos.mbrc.presenters.MainViewPresenter
import com.kelsos.mbrc.presenters.MainViewPresenterImpl
import com.kelsos.mbrc.presenters.MiniControlPresenter
import com.kelsos.mbrc.presenters.MiniControlPresenterImpl
import com.kelsos.mbrc.presenters.PlaylistDialogPresenter
import com.kelsos.mbrc.presenters.PlaylistDialogPresenterImpl
import com.kelsos.mbrc.presenters.PlaylistPresenter
import com.kelsos.mbrc.presenters.PlaylistPresenterImpl
import com.kelsos.mbrc.presenters.PlaylistTrackPresenter
import com.kelsos.mbrc.presenters.PlaylistTrackPresenterImpl
import com.kelsos.mbrc.repository.DeviceRepository
import com.kelsos.mbrc.repository.DeviceRepositoryImpl
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
import com.kelsos.mbrc.viewmodels.MainViewModel
import com.kelsos.mbrc.viewmodels.MainViewModelImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import toothpick.config.Module

@SuppressWarnings("UnusedDeclaration") class RemoteModule : Module() {
  init {
    bind(ObjectMapper::class.java).toProvider(ObjectMapperProvider::class.java).providesSingletonInScope()
    bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java).providesSingletonInScope()
    bind(Retrofit::class.java).toProvider(RetrofitProvider::class.java).providesSingletonInScope()
    bind(MiniControlPresenter::class.java).to(MiniControlPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(MainViewPresenter::class.java).to(MainViewPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(MainViewModel::class.java).to(MainViewModelImpl::class.java).`in`(ContextSingleton::class.java)
    bind(LyricsPresenter::class.java).to(LyricsPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(PlaylistPresenter::class.java).to(PlaylistPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(PlaylistTrackPresenter::class.java).to(PlaylistTrackPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(BrowseGenrePresenter::class.java).to(BrowseGenrePresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(BrowseAlbumPresenter::class.java).to(BrowseAlbumPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(BrowseTrackPresenter::class.java).to(BrowseTrackPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(AlbumTracksPresenter::class.java).to(AlbumTracksPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(GenreArtistsPresenter::class.java).to(GenreArtistsPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(ConnectionManagerPresenter::class.java).to(ConnectionManagerPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(LibraryActivityPresenter::class.java).to(LibraryActivityPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(ArtistAlbumPresenter::class.java).to(ArtistAlbumPresenterImpl::class.java).`in`(ContextSingleton::class.java)
    bind(PlaylistDialogPresenter::class.java).to(PlaylistDialogPresenterImpl::class.java).`in`(ContextSingleton::class.java)

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
    bind(DeviceRepository::class.java).to(DeviceRepositoryImpl::class.java).singletonInScope()

    bind(TrackCache::class.java).to(TrackCacheImpl::class.java).singletonInScope()
    bind(PlayerStateCache::class.java).to(PlayerStateCacheImpl::class.java).singletonInScope()

    bind(TrackService::class.java).toProviderInstance(ApiServiceProvider(TrackService::class.java)).providesSingletonInScope()
    bind(PlayerService::class.java).toProviderInstance(ApiServiceProvider(PlayerService::class.java)).providesSingletonInScope()
    bind(LibraryService::class.java).toProviderInstance(ApiServiceProvider(LibraryService::class.java)).providesSingletonInScope()
    bind(NowPlayingService::class.java).toProviderInstance(ApiServiceProvider(NowPlayingService::class.java)).providesSingletonInScope()
    bind(PlaylistService::class.java).toProviderInstance(ApiServiceProvider(PlaylistService::class.java)).providesSingletonInScope()
    bind(ApiService::class.java).toProviderInstance(ApiServiceProvider(ApiService::class.java)).providesSingletonInScope()
    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()

    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java).providesSingletonInScope()
    bind(com.kelsos.mbrc.utilities.KeyProvider::class.java).to(com.kelsos.mbrc.utilities.KeyProviderImpl::class.java)
    bind(SocketMessageHandler::class.java).singletonInScope()
  }
}
