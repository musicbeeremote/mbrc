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
import com.kelsos.mbrc.networking.*
import com.kelsos.mbrc.networking.client.*
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.connections.DefaultSettingsModel
import com.kelsos.mbrc.networking.connections.DefaultSettingsModelImpl
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscoveryImpl
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCaseImpl
import com.kelsos.mbrc.networking.protocol.*
import com.kelsos.mbrc.networking.protocol.commands.*
import com.kelsos.mbrc.platform.RemoteBroadcastReceiver
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.RemoteSessionManager
import com.kelsos.mbrc.platform.mediasession.RemoteVolumeProvider
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.*
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerViewModel
import com.kelsos.mbrc.ui.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryViewModel
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumEntryAdapter
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumViewModel
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksViewModel
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistEntryAdapter
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistViewModel
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenreViewModel
import com.kelsos.mbrc.ui.navigation.library.genres.GenreEntryAdapter
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackViewModel
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackEntryAdapter
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsViewModel
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManager
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManagerImpl
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingViewModel
import com.kelsos.mbrc.ui.navigation.player.PlayerViewModel
import com.kelsos.mbrc.ui.navigation.player.VolumeDialogViewModel
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter
import com.kelsos.mbrc.ui.navigation.radio.RadioViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx2.asCoroutineDispatcher
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.concurrent.Executors


val appModule = module {
  single { Moshi.Builder().build() }
  single<CoverApi> { CoverApiImpl(get()) }
  single<QueueApi> { QueueApiImpl(get()) }

  single<ConnectionRepository> { ConnectionRepositoryImpl(get(), get(), get(), get()) }

  single<TrackRepository> { TrackRepositoryImpl(get(), get(), get()) }
  single<AlbumRepository> { AlbumRepositoryImpl(get(), get(), get()) }
  single<ArtistRepository> { ArtistRepositoryImpl(get(), get(), get()) }
  single<GenreRepository> { GenreRepositoryImpl(get(), get(), get()) }

  single<NowPlayingRepository> { NowPlayingRepositoryImpl(get(), get(), get()) }
  single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }

  single<AlbumSortingStore> { AlbumSortingStoreImpl(get()) }

  single<MessageSerializer> { MessageSerializerImpl(get()) }


  single<SerializationAdapter> { SerializationAdapterImpl(get()) }
  single<DeserializationAdapter> { DeserializationAdapterImpl(get()) }
  single<DatabaseTransactionRunner> { DatabaseTransactionRunnerImpl(get()) }
  single<RequestManager> { RequestManagerImpl(get(), get(), get(), get()) }

  single<UserActionUseCase> { UserActionUseCaseImpl(get()) }

  single<ClientConnectionUseCase> { ClientConnectionUseCaseImpl(get()) }

  single<SettingsManager> { SettingsManagerImpl(get(), get()) }
  single<PlayingTrackCache> { PlayingTrackCacheImpl(get(), get(), get()) }
  single<ServiceChecker> { ServiceCheckerImpl(get(), get()) }

  single<LibrarySyncUseCase> {
    LibrarySyncUseCaseImpl(get(), get(), get(), get(), get(), get(), get())
  }

  single<RadioRepository> { RadioRepositoryImpl(get(), get(), get()) }
  single<ClientInformationStore> { ClientInformationStoreImpl(get()) }
  single<VolumeInteractor> { VolumeInteractorImpl(get(), get()) }
  single<OutputApi> { OutputApiImpl(get()) }


  //bindInstance { SyncProgressProvider() }

  single<PlayingTrackLiveDataProvider> { PlayingTrackLiveDataProviderImpl(get(), get()) }
  single<PlayerStatusLiveDataProvider> { PlayerStatusLiveDataProviderImpl() }
  single<TrackRatingLiveDataProvider> { TrackRatingLiveDataProviderImpl() }
  single<ConnectionStatusLiveDataProvider> {
    ConnectionStatusLiveDataProviderImpl()
  }

  single<DefaultSettingsLiveDataProvider> {
    DefaultSettingsLiveDataProviderImpl(get())
  }

  single<LyricsLiveDataProvider> { LyricsLiveDataProviderImpl() }

  single<MessageQueue> { MessageQueueImpl() }
  single<MessageHandler> { MessageHandlerImpl(get(), get(), get(), get(), get(), get()) }
  single<CommandExecutor> { CommandExecutorImpl(get()) }

  single<IClientConnectionManager> { ClientConnectionManager(get(), get(), get(), get(), get()) }
  single<CommandFactory> { CommandFactoryImpl() }
  single<MessageDeserializer> { MessageDeserializerImpl(get()) }
  single<UiMessageQueue> { UiMessageQueueImpl(get()) }
  single<RemoteServiceDiscovery> { RemoteServiceDiscoveryImpl(get(), get(), get()) }
  single<ServiceDiscoveryUseCase> { ServiceDiscoveryUseCaseImpl(get(), get()) }
  single<TrackPositionLiveDataProvider> { TrackPositionLiveDataProviderImpl(get()) }

  single<INotificationManager> { SessionNotificationManager(get(), get(), get(), get(), get()) }
  single<IRemoteServiceCore> {
    RemoteServiceCore(get(), get(), get(), get(), get(), get(), get(), get())
  }

  single<CoverModel> { StoredCoverModel }

  single {
    AppRxSchedulers(
      AndroidSchedulers.mainThread(),
      Schedulers.io(),
      Schedulers.from(Executors.newSingleThreadExecutor {
        Thread(it, "database")
      }),
      Schedulers.io()
    )
  }

  single {
    val appRxSchedulers = get<AppRxSchedulers>()
    AppCoroutineDispatchers(
      UI,
      appRxSchedulers.disk.asCoroutineDispatcher(),
      appRxSchedulers.network.asCoroutineDispatcher(),
      appRxSchedulers.database.asCoroutineDispatcher()
    )
  }

  single { ApiBase(get(), get()) }

  single { Room.databaseBuilder(get(), Database::class.java, "cache.db").build() }
  single { get<Database>().genreDao() }
  single { get<Database>().artistDao() }
  single { get<Database>().albumDao() }
  single { get<Database>().trackDao() }
  single { get<Database>().nowPlayingDao() }
  single { get<Database>().playlistDao() }
  single { get<Database>().radioStationDao() }
  single { get<Database>().connectionDao() }


  single { UpdateNowPlayingTrack(get(), get(), get()) }
  single { UpdateCover(get(), get(), get(), get(), get(), get()) }
  single { UpdateRating(get()) }
  single { UpdatePlayerStatus(get(), get()) }
  single { UpdatePlayState(get(), get()) }
  single { UpdateRepeat(get()) }
  single { UpdateVolume(get()) }
  single { UpdateMute(get()) }
  single { UpdateShuffle(get()) }
  single { UpdateLastFm(get()) }
  single { UpdateLyrics(get(), get()) }
  single { UpdateLfmRating(get()) }
  single { UpdateNowPlayingTrackRemoval(get()) }
  single { UpdateNowPlayingTrackMoved(get()) }
  single { UpdatePlaybackPositionCommand(get(), get()) }
  single { UpdatePluginVersionCommand() }
  single { ProtocolPingHandle(get(), get()) }
  single { ProtocolPongHandle() }

  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

  factory { DefaultSettingsModelImpl as DefaultSettingsModel }
  factory { MoveManagerImpl() as MoveManager }

  factory { SocketActivityChecker() }
  factory { RemoteBroadcastReceiver() }
  factory { SessionNotificationManager(get(), get(), get(), get(), get()) }
  factory { RemoteSessionManager(get(), get(), get(), get()) }
  factory { RemoteVolumeProvider(get(), get()) }
}

val uiModule = module {
  viewModel { AlbumTracksViewModel(get()) }
  viewModel { ConnectionManagerViewModel(get(), get(), get()) }
  viewModel { PlayerViewModel(get(), get(), get(), get(), get(), get(), get()) }
  viewModel { BrowseAlbumViewModel(get(), get(), get()) }
  viewModel { BrowseGenreViewModel(get(), get()) }
  viewModel { BrowseArtistViewModel(get(), get(), get()) }
  viewModel { BrowseTrackViewModel(get(), get()) }
  viewModel { MiniControlViewModel(get(), get(), get(), get()) }
  viewModel { LyricsViewModel(get()) }
  viewModel { RadioViewModel(get(), get(), get(), get()) }
  viewModel { NowPlayingViewModel(get(), get(), get(), get(), get()) }
  viewModel { LibraryViewModel(get(), get(), get(), get()) }

  viewModel { VolumeDialogViewModel(get(), get(), get()) }

  factory { RadioAdapter() }
  factory { GenreEntryAdapter() }
  factory { ArtistEntryAdapter() }
  factory { AlbumEntryAdapter() }
  factory { TrackEntryAdapter() }
}