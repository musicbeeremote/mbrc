package com.kelsos.mbrc.di.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusState
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusStateImpl
import com.kelsos.mbrc.content.activestatus.livedata.LyricsState
import com.kelsos.mbrc.content.activestatus.livedata.LyricsStateImpl
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
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.features.radio.repository.RadioRepositoryImpl
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.LibrarySyncUseCaseImpl
import com.kelsos.mbrc.core.IRemoteServiceCore
import com.kelsos.mbrc.core.RemoteServiceCore
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
import com.kelsos.mbrc.platform.widgets.WidgetUpdater
import com.kelsos.mbrc.platform.widgets.WidgetUpdaterImpl
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
import com.kelsos.mbrc.ui.connectionmanager.ConnectionManagerViewModel
import com.kelsos.mbrc.ui.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryViewModel
import com.kelsos.mbrc.ui.navigation.library.SyncProgressProvider
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumAdapter
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumViewModel
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistAdapter
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistViewModel
import com.kelsos.mbrc.ui.navigation.library.genres.GenreAdapter
import com.kelsos.mbrc.ui.navigation.library.genres.GenreViewModel
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackAdapter
import com.kelsos.mbrc.ui.navigation.library.tracks.TrackViewModel
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsViewModel
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManager
import com.kelsos.mbrc.ui.navigation.nowplaying.MoveManagerImpl
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingViewModel
import com.kelsos.mbrc.ui.navigation.player.PlayerViewModel
import com.kelsos.mbrc.ui.navigation.player.VolumeDialogViewModel
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter
import com.kelsos.mbrc.features.radio.presentation.RadioViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import java.util.concurrent.Executors

val appModule = module {
  single { Moshi.Builder().build() }
  single<CoverApi> { create<CoverApiImpl>() }
  single<QueueApi> { create<QueueApiImpl>() }

  single<ConnectionRepository> { create<ConnectionRepositoryImpl>() }

  single<TrackRepository> { create<TrackRepositoryImpl>() }
  single<AlbumRepository> { create<AlbumRepositoryImpl>() }
  single<ArtistRepository> { create<ArtistRepositoryImpl>() }
  single<GenreRepository> { create<GenreRepositoryImpl>() }

  single<NowPlayingRepository> { create<NowPlayingRepositoryImpl>() }
  single<PlaylistRepository> { create<PlaylistRepositoryImpl>() }

  single<SerializationAdapter> { create<SerializationAdapterImpl>() }
  single<DeserializationAdapter> { create<DeserializationAdapterImpl>() }
  single<DatabaseTransactionRunner> { create<DatabaseTransactionRunnerImpl>() }
  single<RequestManager> { create<RequestManagerImpl>() }

  single<UserActionUseCase> { create<UserActionUseCaseImpl>() }

  single<ClientConnectionUseCase> { create<ClientConnectionUseCaseImpl>() }

  single<SettingsManager> { create<SettingsManagerImpl>() }
  single<PlayingTrackCache> { create<PlayingTrackCacheImpl>() }
  single<ServiceChecker> { create<ServiceCheckerImpl>() }

  single<LibrarySyncUseCase> {
    create<LibrarySyncUseCaseImpl>()
  }

  single<RadioRepository> { create<RadioRepositoryImpl>() }
  single<ClientInformationStore> { create<ClientInformationStoreImpl>() }
  single<VolumeModifyUseCase> { create<VolumeModifyUseCaseImpl>() }
  single<OutputApi> { create<OutputApiImpl>() }

  single { create<SyncProgressProvider>() }

  single<PlayingTrackState> { create<PlayingTrackStateImpl>() }
  single<PlayerStatusState> { create<PlayerStatusStateImpl>() }
  single<TrackRatingState> { create<TrackRatingStateImpl>() }
  single<ConnectionStatusState> {
    create<ConnectionStatusStateImpl>()
  }

  single<LyricsState> { create<LyricsStateImpl>() }

  single<MessageQueue> { create<MessageQueueImpl>() }
  single<MessageHandler> { create<MessageHandlerImpl>() }
  single<CommandExecutor> { create<CommandExecutorImpl>() }

  single<IClientConnectionManager> { create<ClientConnectionManager>() }
  single<CommandFactory> { create<CommandFactoryImpl>() }
  single<UiMessageQueue> { create<UiMessageQueueImpl>() }
  single<RemoteServiceDiscovery> { create<RemoteServiceDiscoveryImpl>() }
  single<TrackPositionState> { create<TrackPositionStateImpl>() }

  single<INotificationManager> { create<SessionNotificationManager>() }
  single<IRemoteServiceCore> { create<RemoteServiceCore>() }

  single<CoverModel> { StoredCoverModel }

  single<WidgetUpdater> { create<WidgetUpdaterImpl>() }

  single {
    AppRxSchedulers(
      AndroidSchedulers.mainThread(),
      Schedulers.io(),
      Schedulers.from(Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "database")
      }),
      Schedulers.io()
    )
  }

  single {
    val appRxSchedulers = get<AppRxSchedulers>()
    AppCoroutineDispatchers(
      Dispatchers.Main,
      appRxSchedulers.disk.asCoroutineDispatcher(),
      appRxSchedulers.network.asCoroutineDispatcher(),
      appRxSchedulers.database.asCoroutineDispatcher()
    )
  }

  single { create<ApiBase>() }

  single { Room.databaseBuilder(get(), Database::class.java, "cache.db").build() }
  single { get<Database>().genreDao() }
  single { get<Database>().artistDao() }
  single { get<Database>().albumDao() }
  single { get<Database>().trackDao() }
  single { get<Database>().nowPlayingDao() }
  single { get<Database>().playlistDao() }
  single { get<Database>().radioStationDao() }
  single { get<Database>().connectionDao() }

  single { create<UpdateNowPlayingTrack>() }
  single { create<UpdateCover>() }
  single { create<UpdateRating>() }
  single { create<UpdatePlayerStatus>() }
  single { create<UpdatePlayState>() }
  single { create<UpdateRepeat>() }
  single { create<UpdateVolume>() }
  single { create<UpdateMute>() }
  single { create<UpdateShuffle>() }
  single { create<UpdateLastFm>() }
  single { create<UpdateLyrics>() }
  single { create<UpdateLfmRating>() }
  single { create<UpdateNowPlayingTrackRemoval>() }
  single { create<UpdateNowPlayingTrackMoved>() }
  single { create<UpdatePlaybackPositionCommand>() }
  single { create<UpdatePluginVersionCommand>() }
  single { create<ProtocolPingHandle>() }
  single { create<ProtocolPongHandle>() }

  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

  factory { DefaultSettingsModelImpl as DefaultSettingsModel }
  factory { ClientInformationModelImpl as ClientInformationModel }
  factory { create<MoveManagerImpl>() as MoveManager }

  factory { create<SocketActivityChecker>() }
  factory { create<RemoteBroadcastReceiver>() }
  factory { create<SessionNotificationManager>() }
  factory { create<RemoteSessionManager>() }
  factory { create<RemoteVolumeProvider>() }
}

val uiModule = module {
  viewModel { create<ConnectionManagerViewModel>() }
  viewModel { create<PlayerViewModel>() }
  viewModel { create<AlbumViewModel>() }
  viewModel { create<GenreViewModel>() }
  viewModel { create<ArtistViewModel>() }
  viewModel { create<TrackViewModel>() }
  viewModel { create<MiniControlViewModel>() }
  viewModel { create<LyricsViewModel>() }
  viewModel { create<RadioViewModel>() }
  viewModel { create<NowPlayingViewModel>() }
  viewModel { create<LibraryViewModel>() }

  viewModel { create<VolumeDialogViewModel>() }

  factory { create<RadioAdapter>() }
  factory { create<GenreAdapter>() }
  factory { create<ArtistAdapter>() }
  factory { create<AlbumAdapter>() }
  factory { create<TrackAdapter>() }
}