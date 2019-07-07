package com.kelsos.mbrc.di.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusState
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusStateImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusStateImpl
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackStateImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionStateImpl
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingStateImpl
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.AlbumRepositoryImpl
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepositoryImpl
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.genres.GenreRepositoryImpl
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepositoryImpl
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
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.lyrics.LyricsStateImpl
import com.kelsos.mbrc.features.lyrics.presentation.LyricsAdapter
import com.kelsos.mbrc.features.lyrics.presentation.LyricsViewModel
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.features.nowplaying.domain.MoveManager
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingViewModel
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.features.output.OutputApi
import com.kelsos.mbrc.features.output.OutputApiImpl
import com.kelsos.mbrc.features.output.OutputSelectionViewModel
import com.kelsos.mbrc.features.player.cover.CoverModel
import com.kelsos.mbrc.features.player.cover.StoredCoverModel
import com.kelsos.mbrc.features.playlists.presentation.PlaylistAdapter
import com.kelsos.mbrc.features.playlists.presentation.PlaylistViewModel
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter
import com.kelsos.mbrc.features.radio.presentation.RadioViewModel
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.features.radio.repository.RadioRepositoryImpl
import com.kelsos.mbrc.features.widgets.WidgetUpdater
import com.kelsos.mbrc.features.widgets.WidgetUpdaterImpl
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.ClientConnectionUseCaseImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.ClientConnectionManager
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.client.MessageHandler
import com.kelsos.mbrc.networking.client.MessageHandlerImpl
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.MessageQueueImpl
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
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.CommandExecutorImpl
import com.kelsos.mbrc.networking.protocol.CommandFactory
import com.kelsos.mbrc.networking.protocol.CommandFactoryImpl
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCase
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCaseImpl
import com.kelsos.mbrc.platform.RemoteBroadcastReceiver
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.platform.ServiceCheckerImpl
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.RemoteSessionManager
import com.kelsos.mbrc.platform.mediasession.RemoteVolumeProvider
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.ClientInformationModel
import com.kelsos.mbrc.preferences.ClientInformationModelImpl
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.kelsos.mbrc.preferences.ClientInformationStoreImpl
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.preferences.SettingsManagerImpl
import com.kelsos.mbrc.protocol.ProtocolPingHandle
import com.kelsos.mbrc.protocol.ProtocolPongHandle
import com.kelsos.mbrc.protocol.UpdateCover
import com.kelsos.mbrc.protocol.UpdateLastFm
import com.kelsos.mbrc.protocol.UpdateLfmRating
import com.kelsos.mbrc.protocol.UpdateLyrics
import com.kelsos.mbrc.protocol.UpdateMute
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrack
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrackMoved
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrackRemoval
import com.kelsos.mbrc.protocol.UpdatePlayState
import com.kelsos.mbrc.protocol.UpdatePlaybackPositionCommand
import com.kelsos.mbrc.protocol.UpdatePlayerStatus
import com.kelsos.mbrc.protocol.UpdatePluginVersionCommand
import com.kelsos.mbrc.protocol.UpdateRating
import com.kelsos.mbrc.protocol.UpdateRepeat
import com.kelsos.mbrc.protocol.UpdateShuffle
import com.kelsos.mbrc.protocol.UpdateVolume
import com.kelsos.mbrc.ui.connectionmanager.ConnectionAdapter
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryViewModel
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumAdapter
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumViewModel
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistAdapter
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistViewModel
import com.kelsos.mbrc.ui.navigation.library.genres.GenreAdapter
import com.kelsos.mbrc.ui.navigation.library.genres.GenreViewModel
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackAdapter
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackViewModel
import com.kelsos.mbrc.ui.navigation.player.PlayerViewModel
import com.kelsos.mbrc.ui.navigation.player.RatingDialogViewModel
import com.kelsos.mbrc.ui.navigation.player.VolumeDialogViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.factoryBy
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
  singleBy<VolumeModifyUseCase, VolumeModifyUseCaseImpl>()
  singleBy<OutputApi, OutputApiImpl>()

  singleBy<PlayingTrackState, PlayingTrackStateImpl>()
  singleBy<PlayerStatusState, PlayerStatusStateImpl>()
  singleBy<TrackRatingState, TrackRatingStateImpl>()
  singleBy<ConnectionStatusState, ConnectionStatusStateImpl>()

  singleBy<LyricsState, LyricsStateImpl>()
  single { LyricsAdapter(get(named("diffExecutor"))) }
  single(named("diffExecutor")) {
    Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "diffExecutor")
    } as Executor
  }

  singleBy<MessageQueue, MessageQueueImpl>()
  singleBy<MessageHandler, MessageHandlerImpl>()
  singleBy<CommandExecutor, CommandExecutorImpl>()

  singleBy<IClientConnectionManager, ClientConnectionManager>()
  singleBy<CommandFactory, CommandFactoryImpl>()
  singleBy<UiMessageQueue, UiMessageQueueImpl>()
  singleBy<RemoteServiceDiscovery, RemoteServiceDiscoveryImpl>()
  singleBy<TrackPositionState, TrackPositionStateImpl>()

  singleBy<INotificationManager, SessionNotificationManager>()
  singleBy<IRemoteServiceCore, RemoteServiceCore>()

  singleBy<CoverModel, StoredCoverModel>()

  singleBy<WidgetUpdater, WidgetUpdaterImpl>()

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

  factory<DefaultSettingsModel> { DefaultSettingsModelImpl }
  factoryBy<ClientInformationModel, ClientInformationModelImpl>()
  factoryBy<MoveManager, MoveManagerImpl>()

  factory<SocketActivityChecker>()
  factory<RemoteBroadcastReceiver>()
  factory<SessionNotificationManager>()
  factory<RemoteSessionManager>()
  factory<RemoteVolumeProvider>()
}

val uiModule = module {
  viewModel<ConnectionManagerViewModel>()
  viewModel<PlayerViewModel>()
  viewModel<AlbumViewModel>()
  viewModel<GenreViewModel>()
  viewModel<ArtistViewModel>()
  viewModel<TrackViewModel>()
  viewModel<MiniControlViewModel>()
  viewModel<LyricsViewModel>()
  viewModel<RadioViewModel>()
  viewModel<NowPlayingViewModel>()
  viewModel<LibraryViewModel>()
  viewModel<PlaylistViewModel>()
  viewModel<OutputSelectionViewModel>()
  viewModel<RatingDialogViewModel>()
  viewModel<VolumeDialogViewModel>()

  factory<RadioAdapter>()
  factory<PlaylistAdapter>()
  factory<GenreAdapter>()
  factory<ArtistAdapter>()
  factory<AlbumAdapter>()
  factory<TrackAdapter>()
  factory<ConnectionAdapter>()
}
