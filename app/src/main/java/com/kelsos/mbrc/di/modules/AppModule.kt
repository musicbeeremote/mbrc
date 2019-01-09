package com.kelsos.mbrc.di.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProviderImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProviderImpl
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
import com.kelsos.mbrc.content.nowplaying.cover.CoverModel
import com.kelsos.mbrc.content.nowplaying.cover.StoredCoverModel
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
import com.kelsos.mbrc.covers.CoverCache
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.data.DatabaseTransactionRunner
import com.kelsos.mbrc.data.DatabaseTransactionRunnerImpl
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.DeserializationAdapterImpl
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.data.SerializationAdapterImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.ClientConnectionUseCaseImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.ClientConnectionManager
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.client.MessageDeserializer
import com.kelsos.mbrc.networking.client.MessageDeserializerImpl
import com.kelsos.mbrc.networking.client.MessageHandler
import com.kelsos.mbrc.networking.client.MessageHandlerImpl
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.MessageQueueImpl
import com.kelsos.mbrc.networking.client.MessageSerializer
import com.kelsos.mbrc.networking.client.MessageSerializerImpl
import com.kelsos.mbrc.networking.client.UiMessageQueue
import com.kelsos.mbrc.networking.client.UiMessageQueueImpl
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.UserActionUseCaseImpl
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.connections.DefaultSettingsModel
import com.kelsos.mbrc.networking.connections.DefaultSettingsModelImpl
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscoveryImpl
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCaseImpl
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.CommandExecutorImpl
import com.kelsos.mbrc.networking.protocol.CommandFactory
import com.kelsos.mbrc.networking.protocol.CommandFactoryImpl
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.networking.protocol.VolumeInteractorImpl
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPingHandle
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPongHandle
import com.kelsos.mbrc.networking.protocol.commands.UpdateCover
import com.kelsos.mbrc.networking.protocol.commands.UpdateLastFm
import com.kelsos.mbrc.networking.protocol.commands.UpdateLfmRating
import com.kelsos.mbrc.networking.protocol.commands.UpdateLyrics
import com.kelsos.mbrc.networking.protocol.commands.UpdateMute
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrack
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrackMoved
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrackRemoval
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlayState
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlaybackPositionCommand
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlayerStatus
import com.kelsos.mbrc.networking.protocol.commands.UpdatePluginVersionCommand
import com.kelsos.mbrc.networking.protocol.commands.UpdateRating
import com.kelsos.mbrc.networking.protocol.commands.UpdateRepeat
import com.kelsos.mbrc.networking.protocol.commands.UpdateShuffle
import com.kelsos.mbrc.networking.protocol.commands.UpdateVolume
import com.kelsos.mbrc.platform.RemoteBroadcastReceiver
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.RemoteSessionManager
import com.kelsos.mbrc.platform.mediasession.RemoteVolumeProvider
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.kelsos.mbrc.preferences.ClientInformationStoreImpl
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.preferences.SettingsManagerImpl
import com.kelsos.mbrc.ui.connectionmanager.ConnectionAdapter
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
import com.kelsos.mbrc.ui.navigation.player.RatingDialogViewModel
import com.kelsos.mbrc.ui.navigation.player.VolumeDialogViewModel
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter
import com.kelsos.mbrc.ui.navigation.radio.RadioViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.factoryBy
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy

val appModule = module {
  single { Moshi.Builder().build() }

  singleBy<ConnectionRepository, ConnectionRepositoryImpl>()

  singleBy<TrackRepository, TrackRepositoryImpl>()
  singleBy<AlbumRepository, AlbumRepositoryImpl>()
  singleBy<ArtistRepository, ArtistRepositoryImpl>()
  singleBy<GenreRepository, GenreRepositoryImpl>()

  singleBy<NowPlayingRepository, NowPlayingRepositoryImpl>()
  singleBy<PlaylistRepository, PlaylistRepositoryImpl>()
  single<CoverCache>()

  singleBy<MessageSerializer, MessageSerializerImpl>()

  singleBy<SerializationAdapter, SerializationAdapterImpl>()
  singleBy<DeserializationAdapter, DeserializationAdapterImpl>()
  singleBy<DatabaseTransactionRunner, DatabaseTransactionRunnerImpl>()
  singleBy<RequestManager, RequestManagerImpl>()

  singleBy<UserActionUseCase, UserActionUseCaseImpl>()

  singleBy<ClientConnectionUseCase, ClientConnectionUseCaseImpl>()

  singleBy<SettingsManager, SettingsManagerImpl>()
  singleBy<PlayingTrackCache, PlayingTrackCacheImpl>()
  singleBy<ServiceChecker, ServiceCheckerImpl>()

  singleBy<LibrarySyncUseCase, LibrarySyncUseCaseImpl>()

  singleBy<RadioRepository, RadioRepositoryImpl>()
  singleBy<ClientInformationStore, ClientInformationStoreImpl>()
  singleBy<VolumeInteractor, VolumeInteractorImpl>()
  singleBy<OutputApi, OutputApiImpl>()

  singleBy<PlayingTrackLiveDataProvider, PlayingTrackLiveDataProviderImpl>()
  singleBy<PlayerStatusLiveDataProvider, PlayerStatusLiveDataProviderImpl>()
  singleBy<TrackRatingLiveDataProvider, TrackRatingLiveDataProviderImpl>()
  singleBy<ConnectionStatusLiveDataProvider, ConnectionStatusLiveDataProviderImpl>()

  singleBy<DefaultSettingsLiveDataProvider, DefaultSettingsLiveDataProviderImpl>()

  singleBy<LyricsLiveDataProvider, LyricsLiveDataProviderImpl>()

  singleBy<MessageQueue, MessageQueueImpl>()
  singleBy<MessageHandler, MessageHandlerImpl>()
  singleBy<CommandExecutor, CommandExecutorImpl>()

  singleBy<IClientConnectionManager, ClientConnectionManager>()
  singleBy<CommandFactory, CommandFactoryImpl>()
  singleBy<MessageDeserializer, MessageDeserializerImpl>()
  singleBy<UiMessageQueue, UiMessageQueueImpl>()
  singleBy<RemoteServiceDiscovery, RemoteServiceDiscoveryImpl>()
  singleBy<ServiceDiscoveryUseCase, ServiceDiscoveryUseCaseImpl>()
  singleBy<TrackPositionLiveDataProvider, TrackPositionLiveDataProviderImpl>()

  singleBy<INotificationManager, SessionNotificationManager>()
  singleBy<IRemoteServiceCore, RemoteServiceCore>()

  singleBy<CoverModel, StoredCoverModel>()

  single {
    AppCoroutineDispatchers(
      Dispatchers.Main,
      Dispatchers.IO,
      Dispatchers.IO,
      Dispatchers.IO
    )
  }

  single<ApiBase>()

  single { Room.databaseBuilder(get(), Database::class.java, "cache.db").build() }
  single { get<Database>().genreDao() }
  single { get<Database>().artistDao() }
  single { get<Database>().albumDao() }
  single { get<Database>().trackDao() }
  single { get<Database>().nowPlayingDao() }
  single { get<Database>().playlistDao() }
  single { get<Database>().radioStationDao() }
  single { get<Database>().connectionDao() }

  single<UpdateNowPlayingTrack>()
  single<UpdateCover>()
  single<UpdateRating>()
  single<UpdatePlayerStatus>()
  single<UpdatePlayState>()
  single<UpdateRepeat>()
  single<UpdateVolume>()
  single<UpdateMute>()
  single<UpdateShuffle>()
  single<UpdateLastFm>()
  single<UpdateLyrics>()
  single<UpdateLfmRating>()
  single<UpdateNowPlayingTrackRemoval>()
  single<UpdateNowPlayingTrackMoved>()
  single<UpdatePlaybackPositionCommand>()
  single<UpdatePluginVersionCommand>()
  single<ProtocolPingHandle>()
  single<ProtocolPongHandle>()

  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

  factoryBy<DefaultSettingsModel, DefaultSettingsModelImpl>()
  factoryBy<MoveManager, MoveManagerImpl>()

  factory<SocketActivityChecker>()
  factory<RemoteBroadcastReceiver>()
  factory<SessionNotificationManager>()
  factory<RemoteSessionManager>()
  factory<RemoteVolumeProvider>()
}

val uiModule = module {
  viewModel<AlbumTracksViewModel>()
  viewModel<ConnectionManagerViewModel>()
  viewModel<PlayerViewModel>()
  viewModel<BrowseAlbumViewModel>()
  viewModel<BrowseGenreViewModel>()
  viewModel<BrowseArtistViewModel>()
  viewModel<BrowseTrackViewModel>()
  viewModel<MiniControlViewModel>()
  viewModel<LyricsViewModel>()
  viewModel<RadioViewModel>()
  viewModel<NowPlayingViewModel>()
  viewModel<LibraryViewModel>()
  viewModel<RatingDialogViewModel>()
  viewModel<VolumeDialogViewModel>()

  factory<RadioAdapter>()
  factory<GenreEntryAdapter>()
  factory<ArtistEntryAdapter>()
  factory<AlbumEntryAdapter>()
  factory<TrackEntryAdapter>()
  factory<ConnectionAdapter>()
}
