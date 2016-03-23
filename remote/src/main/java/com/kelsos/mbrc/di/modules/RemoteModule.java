package com.kelsos.mbrc.di.modules;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationManagerCompat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.kelsos.mbrc.cache.PlayerStateCache;
import com.kelsos.mbrc.cache.PlayerStateCacheImpl;
import com.kelsos.mbrc.cache.TrackCache;
import com.kelsos.mbrc.cache.TrackCacheImpl;
import com.kelsos.mbrc.di.providers.ApiServiceProvider;
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider;
import com.kelsos.mbrc.di.providers.ObjectMapperProvider;
import com.kelsos.mbrc.di.providers.OkHttpClientProvider;
import com.kelsos.mbrc.di.providers.RetrofitProvider;
import com.kelsos.mbrc.interactors.ArtistInteractor;
import com.kelsos.mbrc.interactors.ArtistInteractorImpl;
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor;
import com.kelsos.mbrc.interactors.LibraryAlbumInteractorImpl;
import com.kelsos.mbrc.interactors.LibraryStatusInteractor;
import com.kelsos.mbrc.interactors.LibraryStatusInteractorImpl;
import com.kelsos.mbrc.interactors.LibraryTrackInteractor;
import com.kelsos.mbrc.interactors.LibraryTrackInteractorImpl;
import com.kelsos.mbrc.interactors.MuteInteractor;
import com.kelsos.mbrc.interactors.MuteInteractorImpl;
import com.kelsos.mbrc.interactors.NowPlayingListInteractor;
import com.kelsos.mbrc.interactors.NowPlayingListInteractorImpl;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.PlayerInteractorImpl;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.PlayerStateInteractorImpl;
import com.kelsos.mbrc.interactors.PlaylistInteractor;
import com.kelsos.mbrc.interactors.PlaylistInteractorImpl;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractorImpl;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.ShuffleInteractorImpl;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractorImpl;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractorImpl;
import com.kelsos.mbrc.interactors.TrackLyricsInteractor;
import com.kelsos.mbrc.interactors.TrackLyricsInteractorImpl;
import com.kelsos.mbrc.interactors.TrackPositionInteractor;
import com.kelsos.mbrc.interactors.TrackPositionInteractorImpl;
import com.kelsos.mbrc.interactors.TrackRatingInteractor;
import com.kelsos.mbrc.interactors.TrackRatingInteractorImpl;
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor;
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractorImpl;
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor;
import com.kelsos.mbrc.interactors.library.GenreArtistInteractorImpl;
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractor;
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractorImpl;
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractor;
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractorImpl;
import com.kelsos.mbrc.presenters.AlbumTracksPresenter;
import com.kelsos.mbrc.presenters.AlbumTracksPresenterImpl;
import com.kelsos.mbrc.presenters.ArtistAlbumPresenter;
import com.kelsos.mbrc.presenters.ArtistAlbumPresenterImpl;
import com.kelsos.mbrc.presenters.BrowseAlbumPresenter;
import com.kelsos.mbrc.presenters.BrowseAlbumPresenterImpl;
import com.kelsos.mbrc.presenters.BrowseGenrePresenter;
import com.kelsos.mbrc.presenters.BrowseGenrePresenterImpl;
import com.kelsos.mbrc.presenters.BrowseTrackPresenter;
import com.kelsos.mbrc.presenters.BrowseTrackPresenterImpl;
import com.kelsos.mbrc.presenters.DeviceManagerPresenter;
import com.kelsos.mbrc.presenters.DeviceManagerPresenterImpl;
import com.kelsos.mbrc.presenters.GenreArtistsPresenter;
import com.kelsos.mbrc.presenters.GenreArtistsPresenterImpl;
import com.kelsos.mbrc.presenters.LibraryActivityPresenter;
import com.kelsos.mbrc.presenters.LibraryActivityPresenterImpl;
import com.kelsos.mbrc.presenters.LyricsPresenter;
import com.kelsos.mbrc.presenters.LyricsPresenterImpl;
import com.kelsos.mbrc.presenters.MainViewPresenter;
import com.kelsos.mbrc.presenters.MainViewPresenterImpl;
import com.kelsos.mbrc.presenters.MiniControlPresenter;
import com.kelsos.mbrc.presenters.MiniControlPresenterImpl;
import com.kelsos.mbrc.presenters.PlaylistPresenter;
import com.kelsos.mbrc.presenters.PlaylistPresenterImpl;
import com.kelsos.mbrc.presenters.PlaylistTrackPresenter;
import com.kelsos.mbrc.presenters.PlaylistTrackPresenterImpl;
import com.kelsos.mbrc.repository.DeviceRepository;
import com.kelsos.mbrc.repository.DeviceRepositoryImpl;
import com.kelsos.mbrc.repository.NowPlayingRepository;
import com.kelsos.mbrc.repository.NowPlayingRepositoryImpl;
import com.kelsos.mbrc.repository.PlaylistRepository;
import com.kelsos.mbrc.repository.PlaylistRepositoryImpl;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.repository.TrackRepositoryImpl;
import com.kelsos.mbrc.repository.library.AlbumRepository;
import com.kelsos.mbrc.repository.library.AlbumRepositoryImpl;
import com.kelsos.mbrc.repository.library.ArtistRepository;
import com.kelsos.mbrc.repository.library.ArtistRepositoryImpl;
import com.kelsos.mbrc.repository.library.CoverRepository;
import com.kelsos.mbrc.repository.library.CoverRepositoryImpl;
import com.kelsos.mbrc.repository.library.GenreRepository;
import com.kelsos.mbrc.repository.library.GenreRepositoryImpl;
import com.kelsos.mbrc.services.api.LibraryService;
import com.kelsos.mbrc.services.api.NowPlayingService;
import com.kelsos.mbrc.services.api.PlayerService;
import com.kelsos.mbrc.services.api.PlaylistService;
import com.kelsos.mbrc.services.api.TrackService;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.RxBusImpl;
import com.kelsos.mbrc.viewmodels.MainViewModel;
import com.kelsos.mbrc.viewmodels.MainViewModelImpl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import roboguice.inject.ContextSingleton;
import roboguice.inject.fragment.SupportFragmentManagerProvider;

@SuppressWarnings("UnusedDeclaration") public class RemoteModule extends AbstractModule {
  public void configure() {
    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
    bind(OkHttpClient.class).toProvider(OkHttpClientProvider.class).in(Singleton.class);
    bind(Retrofit.class).toProvider(RetrofitProvider.class).in(Singleton.class);
    bind(MiniControlPresenter.class).to(MiniControlPresenterImpl.class).in(ContextSingleton.class);
    bind(MainViewPresenter.class).to(MainViewPresenterImpl.class).in(ContextSingleton.class);
    bind(MainViewModel.class).to(MainViewModelImpl.class).in(ContextSingleton.class);
    bind(LyricsPresenter.class).to(LyricsPresenterImpl.class).in(ContextSingleton.class);
    bind(PlaylistPresenter.class).to(PlaylistPresenterImpl.class).in(ContextSingleton.class);
    bind(PlaylistTrackPresenter.class).to(PlaylistTrackPresenterImpl.class).in(ContextSingleton.class);
    bind(BrowseGenrePresenter.class).to(BrowseGenrePresenterImpl.class).in(ContextSingleton.class);
    bind(BrowseAlbumPresenter.class).to(BrowseAlbumPresenterImpl.class).in(ContextSingleton.class);
    bind(BrowseTrackPresenter.class).to(BrowseTrackPresenterImpl.class).in(ContextSingleton.class);
    bind(AlbumTracksPresenter.class).to(AlbumTracksPresenterImpl.class).in(ContextSingleton.class);
    bind(GenreArtistsPresenter.class).to(GenreArtistsPresenterImpl.class).in(ContextSingleton.class);
    bind(DeviceManagerPresenter.class).to(DeviceManagerPresenterImpl.class).in(ContextSingleton.class);
    bind(LibraryActivityPresenter.class).to(LibraryActivityPresenterImpl.class).in(ContextSingleton.class);
    bind(ArtistAlbumPresenter.class).to(ArtistAlbumPresenterImpl.class).in(ContextSingleton.class);

    bind(TrackInfoInteractor.class).to(TrackInfoInteractorImpl.class);
    bind(TrackRatingInteractor.class).to(TrackRatingInteractorImpl.class);
    bind(TrackCoverInteractor.class).to(TrackCoverInteractorImpl.class);
    bind(TrackLyricsInteractor.class).to(TrackLyricsInteractorImpl.class);
    bind(TrackPositionInteractor.class).to(TrackPositionInteractorImpl.class);
    bind(PlayerInteractor.class).to(PlayerInteractorImpl.class);
    bind(ShuffleInteractor.class).to(ShuffleInteractorImpl.class);
    bind(RepeatInteractor.class).to(RepeatInteractorImpl.class);
    bind(PlayerStateInteractor.class).to(PlayerStateInteractorImpl.class);
    bind(GenreArtistInteractor.class).to(GenreArtistInteractorImpl.class);
    bind(ArtistAlbumInteractor.class).to(ArtistAlbumInteractorImpl.class);
    bind(ArtistInteractor.class).to(ArtistInteractorImpl.class);

    bind(MuteInteractor.class).to(MuteInteractorImpl.class);
    bind(NowPlayingListInteractor.class).to(NowPlayingListInteractorImpl.class);
    bind(NowPlayingActionInteractor.class).to(NowPlayingActionInteractorImpl.class);
    bind(PlaylistInteractor.class).to(PlaylistInteractorImpl.class);
    bind(LibraryTrackInteractor.class).to(LibraryTrackInteractorImpl.class);
    bind(PlaylistTrackInteractor.class).to(PlaylistTrackInteractorImpl.class);

    bind(LibraryAlbumInteractor.class).to(LibraryAlbumInteractorImpl.class);
    bind(LibraryStatusInteractor.class).to(LibraryStatusInteractorImpl.class);

    bind(TrackRepository.class).to(TrackRepositoryImpl.class).in(Singleton.class);

    bind(ArtistRepository.class).to(ArtistRepositoryImpl.class).in(Singleton.class);
    bind(AlbumRepository.class).to(AlbumRepositoryImpl.class).in(Singleton.class);
    bind(GenreRepository.class).to(GenreRepositoryImpl.class).in(Singleton.class);
    bind(com.kelsos.mbrc.repository.library.TrackRepository.class)
        .to(com.kelsos.mbrc.repository.library.TrackRepositoryImpl.class)
        .in(Singleton.class);

    bind(CoverRepository.class).to(CoverRepositoryImpl.class).in(Singleton.class);

    bind(NowPlayingRepository.class).to(NowPlayingRepositoryImpl.class).in(Singleton.class);
    bind(PlaylistRepository.class).to(PlaylistRepositoryImpl.class).in(Singleton.class);
    bind(DeviceRepository.class).to(DeviceRepositoryImpl.class).in(Singleton.class);

    bind(TrackCache.class).to(TrackCacheImpl.class).in(Singleton.class);
    bind(PlayerStateCache.class).to(PlayerStateCacheImpl.class).in(Singleton.class);

    bind(TrackService.class).toProvider(new ApiServiceProvider<>(TrackService.class)).in(Singleton.class);
    bind(PlayerService.class).toProvider(new ApiServiceProvider<>(PlayerService.class)).in(Singleton.class);
    bind(LibraryService.class).toProvider(new ApiServiceProvider<>(LibraryService.class)).in(Singleton.class);
    bind(NowPlayingService.class).toProvider(new ApiServiceProvider<>(NowPlayingService.class)).in(Singleton.class);
    bind(PlaylistService.class).toProvider(new ApiServiceProvider<>(PlaylistService.class)).in(Singleton.class);
    bind(RxBus.class).to(RxBusImpl.class).in(Singleton.class);

    bind(NotificationManagerCompat.class).toProvider(NotificationManagerCompatProvider.class).in(Singleton.class);
    bind(FragmentManager.class).toProvider(SupportFragmentManagerProvider.class).in(ContextSingleton.class);
  }
}
