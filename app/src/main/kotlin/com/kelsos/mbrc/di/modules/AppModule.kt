package com.kelsos.mbrc.di.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.content.activestatus.livedata.*
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.AlbumRepositoryImpl
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepositoryImpl
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepositoryImpl
import com.kelsos.mbrc.content.nowplaying.cover.CoverApi
import com.kelsos.mbrc.content.nowplaying.cover.CoverApiImpl
import com.kelsos.mbrc.content.nowplaying.cover.CoverModel
import com.kelsos.mbrc.content.nowplaying.cover.StoredCoverModel
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.nowplaying.queue.QueueApiImpl
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputApiImpl
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioRepositoryImpl
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.LibrarySyncUseCaseImpl
import com.kelsos.mbrc.core.IRemoteServiceCore
import com.kelsos.mbrc.core.RemoteServiceCore
import com.kelsos.mbrc.data.*
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.ClientConnectionUseCaseImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.client.*
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscoveryImpl
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCaseImpl
import com.kelsos.mbrc.networking.protocol.*
import com.kelsos.mbrc.networking.protocol.commands.*
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.*
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerPresenter
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerPresenterImpl
import com.kelsos.mbrc.ui.dialogs.OutputSelectionPresenter
import com.kelsos.mbrc.ui.dialogs.OutputSelectionPresenterImpl
import com.kelsos.mbrc.ui.minicontrol.MiniControlPresenter
import com.kelsos.mbrc.ui.minicontrol.MiniControlPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.LibraryPresenter
import com.kelsos.mbrc.ui.navigation.library.LibraryPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumPresenter
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksPresenter
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsPresenter
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistPresenter
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsPresenter
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsPresenterImpl
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenrePresenter
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenrePresenterImpl
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackPresenter
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackPresenterImpl
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsPresenter
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsPresenterImpl
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManager
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManagerImpl
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingPresenter
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingPresenterImpl
import com.kelsos.mbrc.ui.navigation.player.*
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistPresenter
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistPresenterImpl
import com.kelsos.mbrc.ui.navigation.radio.RadioPresenter
import com.kelsos.mbrc.ui.navigation.radio.RadioPresenterImpl
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx2.asCoroutineDispatcher
import org.koin.dsl.module.applicationContext
import java.util.concurrent.Executors


val appModule = applicationContext {
  bean { Moshi.Builder().build() }
  bean<CoverApi> { CoverApiImpl(get()) }
  bean<QueueApi> { QueueApiImpl(get()) }

  bean<ConnectionRepository> { ConnectionRepositoryImpl(get(), get(), get(), get()) }

  bean<TrackRepository> { TrackRepositoryImpl(get(), get(), get()) }
  bean<AlbumRepository> { AlbumRepositoryImpl(get(), get(), get()) }
  bean<ArtistRepository> { ArtistRepositoryImpl(get(), get(), get()) }
  bean<GenreRepository> { GenreRepositoryImpl(get(), get(), get()) }

  bean<NowPlayingRepository> { NowPlayingRepositoryImpl(get(), get(), get()) }
  bean<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }

  bean<AlbumSortingStore> { AlbumSortingStoreImpl(get()) }

  bean<MessageSerializer> { MessageSerializerImpl(get()) }


  bean<SerializationAdapter> { SerializationAdapterImpl(get()) }
  bean<DeserializationAdapter> { DeserializationAdapterImpl(get()) }
  bean<DatabaseTransactionRunner> { DatabaseTransactionRunnerImpl(get()) }
  bean<RequestManager> { RequestManagerImpl(get(), get(), get(), get()) }

  bean<UserActionUseCase> { UserActionUseCaseImpl(get()) }

  bean<ClientConnectionUseCase> { ClientConnectionUseCaseImpl(get()) }

  bean<SettingsManager> { SettingsManagerImpl(get(), get()) }
  bean<PlayingTrackCache> { PlayingTrackCacheImpl(get(), get(), get()) }
  bean<ServiceChecker> { ServiceCheckerImpl(get(), get()) }

  bean<LibrarySyncUseCase> {
    LibrarySyncUseCaseImpl(get(), get(), get(), get(), get(), get(), get())
  }

  bean<RadioRepository> { RadioRepositoryImpl(get(), get(), get()) }
  bean<ClientInformationStore> { ClientInformationStoreImpl(get()) }
  bean<VolumeInteractor> { VolumeInteractorImpl(get(), get()) }
  bean<OutputApi> { OutputApiImpl(get()) }


  //bindInstance { SyncProgressProvider() }

  bean<PlayingTrackLiveDataProvider> { PlayingTrackLiveDataProviderImpl(get(), get()) }
  bean<PlayerStatusLiveDataProvider> { PlayerStatusLiveDataProviderImpl() }
  bean<TrackRatingLiveDataProvider> { TrackRatingLiveDataProviderImpl() }
  bean<ConnectionStatusLiveDataProvider> {
    ConnectionStatusLiveDataProviderImpl()
  }

  bean<DefaultSettingsLiveDataProvider> {
    DefaultSettingsLiveDataProviderImpl(get())
  }

  bean<LyricsLiveDataProvider> { LyricsLiveDataProviderImpl() }

  bean<MessageQueue> { MessageQueueImpl() }
  bean<MessageHandler> { MessageHandlerImpl(get(), get(), get(), get(), get(), get()) }
  bean<CommandExecutor> { CommandExecutorImpl(get()) }

  bean<IClientConnectionManager> { ClientConnectionManager(get(), get(), get(), get(), get()) }
  bean<CommandFactory> { CommandFactoryImpl() }
  bean<MessageDeserializer> { MessageDeserializerImpl(get()) }
  bean<UiMessageQueue> { UiMessageQueueImpl(get()) }
  bean<RemoteServiceDiscovery> { RemoteServiceDiscoveryImpl(get(), get(), get()) }
  bean<ServiceDiscoveryUseCase> { ServiceDiscoveryUseCaseImpl(get(), get()) }
  bean<TrackPositionLiveDataProvider> { TrackPositionLiveDataProviderImpl(get()) }

  bean<INotificationManager> { SessionNotificationManager(get(), get(), get(), get(), get()) }
  bean<IRemoteServiceCore> {
    RemoteServiceCore(get(), get(), get(), get(), get(), get(), get(), get())
  }

  bean<CoverModel> { StoredCoverModel }

  bean {
    AppRxSchedulers(
      AndroidSchedulers.mainThread(),
      Schedulers.io(),
      Schedulers.from(Executors.newSingleThreadExecutor {
        Thread(it, "database")
      }),
      Schedulers.io()
    )
  }

  bean {
    val appRxSchedulers = get<AppRxSchedulers>()
    AppCoroutineDispatchers(
      UI,
      appRxSchedulers.disk.asCoroutineDispatcher(),
      appRxSchedulers.network.asCoroutineDispatcher(),
      appRxSchedulers.database.asCoroutineDispatcher()
    )
  }

  bean { Room.databaseBuilder(get(), Database::class.java, "cache.db").build() }
  bean { get<Database>().genreDao() }
  bean { get<Database>().artistDao() }
  bean { get<Database>().albumDao() }
  bean { get<Database>().trackDao() }
  bean { get<Database>().nowPlayingDao() }
  bean { get<Database>().playlistDao() }
  bean { get<Database>().radioStationDao() }
  bean { get<Database>().connectionDao() }


  bean { UpdateNowPlayingTrack(get(), get(), get()) }
  bean { UpdateCover(get(), get(), get(), get(), get(), get(), get()) }
  bean { UpdateRating(get()) }
  bean { UpdatePlayerStatus(get(), get()) }
  bean { UpdatePlayState(get(), get()) }
  bean { UpdateRepeat(get()) }
  bean { UpdateVolume(get()) }
  bean { UpdateMute(get()) }
  bean { UpdateShuffle(get()) }
  bean { UpdateLastFm(get()) }
  bean { UpdateLyrics(get(), get()) }
  bean { UpdateLfmRating(get()) }
  bean { UpdateNowPlayingTrackRemoval(get()) }
  bean { UpdateNowPlayingTrackMoved(get()) }
  bean { UpdatePlaybackPositionCommand(get(), get()) }
  bean { UpdatePluginVersionCommand() }
  bean { ProtocolPingHandle(get(), get()) }
  bean { ProtocolPongHandle() }

  bean<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
}

val uiModule = applicationContext {
  factory { PlaylistPresenterImpl(get(), get(), get()) as PlaylistPresenter }
  factory { RadioPresenterImpl(get(), get(), get()) as RadioPresenter }
  factory { LyricsPresenterImpl(get()) as LyricsPresenter }
  factory { LibraryPresenterImpl(get(), get(), get(), get()) as LibraryPresenter }
  factory {
    PlayerPresenterImpl(get(), get(), get(), get(), get(), get(), get()) as PlayerPresenter
  }
  factory {
    BrowseAlbumPresenterImpl(get(), get(), get()) as BrowseAlbumPresenter
  }

  factory { AlbumTracksPresenterImpl(get()) as AlbumTracksPresenter }
  factory { ArtistAlbumsPresenterImpl(get()) as ArtistAlbumsPresenter }
  factory { BrowseArtistPresenterImpl(get(), get(), get()) as BrowseArtistPresenter }
  factory { GenreArtistsPresenterImpl(get(), get()) as GenreArtistsPresenter }
  factory { BrowseGenrePresenterImpl(get(), get()) as BrowseGenrePresenter }
  factory { BrowseTrackPresenterImpl(get(), get()) as BrowseTrackPresenter }

  factory { NowPlayingPresenterImpl(get(), get(), get(), get(), get()) as NowPlayingPresenter }
  factory { MoveManagerImpl() as MoveManager }
  factory { OutputSelectionPresenterImpl(get(), get()) as OutputSelectionPresenter }
  factory { ConnectionManagerPresenterImpl(get(), get(), get()) as ConnectionManagerPresenter }
  factory { VolumeDialogPresenterImpl(get(), get(), get()) as VolumeDialogPresenter }
  factory { MiniControlPresenterImpl(get(), get(), get()) as MiniControlPresenter }
  factory { RatingDialogPresenterImpl(get(), get()) as RatingDialogPresenter }
}