package com.kelsos.mbrc

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.work.WorkManager
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.AppRxSchedulers
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
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputApiImpl
import com.kelsos.mbrc.core.IRemoteServiceCore
import com.kelsos.mbrc.core.RemoteServiceCore
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.data.DatabaseTransactionRunner
import com.kelsos.mbrc.data.DatabaseTransactionRunnerImpl
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.DeserializationAdapterImpl
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.data.SerializationAdapterImpl
import com.kelsos.mbrc.features.library.presentation.AlbumAdapter
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.library.presentation.AlbumViewModelImpl
import com.kelsos.mbrc.features.library.presentation.ArtistAdapter
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.library.presentation.ArtistViewModelImpl
import com.kelsos.mbrc.features.library.presentation.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.library.presentation.GenreViewModelImpl
import com.kelsos.mbrc.features.library.presentation.LibraryFragment
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.presentation.LibraryViewModel
import com.kelsos.mbrc.features.library.presentation.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.library.presentation.TrackViewModelImpl
import com.kelsos.mbrc.features.library.presentation.details.LibraryAlbumTracksFragment
import com.kelsos.mbrc.features.library.presentation.details.LibraryArtistAlbumsFragment
import com.kelsos.mbrc.features.library.presentation.details.LibraryGenreArtistsFragment
import com.kelsos.mbrc.features.library.presentation.details.viemodels.AlbumTrackViewModel
import com.kelsos.mbrc.features.library.presentation.details.viemodels.AlbumTrackViewModelImpl
import com.kelsos.mbrc.features.library.presentation.details.viemodels.ArtistAlbumViewModel
import com.kelsos.mbrc.features.library.presentation.details.viemodels.ArtistAlbumViewModelImpl
import com.kelsos.mbrc.features.library.presentation.details.viemodels.GenreArtistViewModel
import com.kelsos.mbrc.features.library.presentation.details.viemodels.GenreArtistViewModelImpl
import com.kelsos.mbrc.features.library.presentation.screens.AlbumScreen
import com.kelsos.mbrc.features.library.presentation.screens.ArtistScreen
import com.kelsos.mbrc.features.library.presentation.screens.GenreScreen
import com.kelsos.mbrc.features.library.presentation.screens.TrackScreen
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.AlbumRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepositoryImpl
import com.kelsos.mbrc.features.library.sync.LibrarySyncUseCase
import com.kelsos.mbrc.features.library.sync.LibrarySyncUseCaseImpl
import com.kelsos.mbrc.features.library.sync.SyncStatProvider
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.features.library.sync.SyncWorkHandlerImpl
import com.kelsos.mbrc.features.library.sync.SyncWorker
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.lyrics.LyricsStateImpl
import com.kelsos.mbrc.features.lyrics.presentation.LyricsAdapter
import com.kelsos.mbrc.features.lyrics.presentation.LyricsViewModel
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.minicontrol.MiniControlFactoryImpl
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.features.nowplaying.domain.MoveManager
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingViewModel
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.features.player.cover.CoverApi
import com.kelsos.mbrc.features.player.cover.CoverApiImpl
import com.kelsos.mbrc.features.player.cover.CoverModel
import com.kelsos.mbrc.features.player.cover.StoredCoverModel
import com.kelsos.mbrc.features.playlists.presentation.PlaylistViewModel
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.features.queue.QueueApi
import com.kelsos.mbrc.features.queue.QueueApiImpl
import com.kelsos.mbrc.features.queue.QueueUseCase
import com.kelsos.mbrc.features.queue.QueueUseCaseImpl
import com.kelsos.mbrc.features.queue.QueueWorker
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter
import com.kelsos.mbrc.features.radio.presentation.RadioViewModel
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.features.radio.repository.RadioRepositoryImpl
import com.kelsos.mbrc.features.widgets.WidgetUpdater
import com.kelsos.mbrc.features.widgets.WidgetUpdaterImpl
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.features.work.WorkHandlerImpl
import com.kelsos.mbrc.logging.LogHelper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.ClientConnectionUseCaseImpl
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.ClientConnectionManager
import com.kelsos.mbrc.networking.client.ConnectivityVerifier
import com.kelsos.mbrc.networking.client.ConnectivityVerifierImpl
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
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import com.kelsos.mbrc.platform.mediasession.RemoteSessionManager
import com.kelsos.mbrc.platform.mediasession.RemoteVolumeProvider
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import com.kelsos.mbrc.preferences.ClientInformationModel
import com.kelsos.mbrc.preferences.ClientInformationModelImpl
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.kelsos.mbrc.preferences.ClientInformationStoreImpl
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.preferences.SettingsManagerImpl
import com.kelsos.mbrc.protocol.ProtocolPingHandle
import com.kelsos.mbrc.protocol.ProtocolPongHandle
import com.kelsos.mbrc.protocol.ProtocolVersionUpdate
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
import com.kelsos.mbrc.ui.helpfeedback.FeedbackViewModel
import com.kelsos.mbrc.ui.navigation.player.PlayerViewModel
import com.kelsos.mbrc.ui.navigation.player.VolumeDialogViewModel
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.component.KoinApiExtension
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import org.koin.experimental.builder.factoryBy
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@OptIn(KoinApiExtension::class)
val appModule = module {
  single { Moshi.Builder().build() }
  singleBy<CoverApi, CoverApiImpl>()
  singleBy<QueueApi, QueueApiImpl>()
  singleBy<QueueUseCase, QueueUseCaseImpl>()

  singleBy<ConnectionRepository, ConnectionRepositoryImpl>()

  singleBy<TrackRepository, TrackRepositoryImpl>()
  singleBy<AlbumRepository, AlbumRepositoryImpl>()
  singleBy<ArtistRepository, ArtistRepositoryImpl>()
  singleBy<GenreRepository, GenreRepositoryImpl>()

  singleBy<NowPlayingRepository, NowPlayingRepositoryImpl>()
  singleBy<PlaylistRepository, PlaylistRepositoryImpl>()
  single<SyncStatProvider>()

  singleBy<SerializationAdapter, SerializationAdapterImpl>()
  singleBy<DeserializationAdapter, DeserializationAdapterImpl>()
  singleBy<DatabaseTransactionRunner, DatabaseTransactionRunnerImpl>()
  singleBy<RequestManager, RequestManagerImpl>()

  singleBy<UserActionUseCase, UserActionUseCaseImpl>()

  singleBy<ClientConnectionUseCase, ClientConnectionUseCaseImpl>()

  singleBy<SettingsManager, SettingsManagerImpl>()
  singleBy<PlayingTrackCache, PlayingTrackCacheImpl>()

  singleBy<LibrarySyncUseCase, LibrarySyncUseCaseImpl>()
  singleBy<SyncWorkHandler, SyncWorkHandlerImpl>()

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

  single<CoverModel> { StoredCoverModel }

  single<DefaultActionPreferenceStore>()

  singleBy<WidgetUpdater, WidgetUpdaterImpl>()
  single<MiniControlFactory> { MiniControlFactoryImpl }

  single {
    AppRxSchedulers(
      AndroidSchedulers.mainThread(),
      Schedulers.io(),
      Schedulers.from(
        Executors.newSingleThreadExecutor { runnable ->
          Thread(runnable, "database")
        }
      ),
      Schedulers.io()
    )
  }

  single {
    val appRxSchedulers = get<AppRxSchedulers>()
    AppCoroutineDispatchers(
      Dispatchers.Main,
      appRxSchedulers.disk.asCoroutineDispatcher(),
      appRxSchedulers.database.asCoroutineDispatcher(),
      appRxSchedulers.network.asCoroutineDispatcher(),
    )
  }

  single {
    WorkManager.getInstance(get())
  }
  singleBy<WorkHandler, WorkHandlerImpl>()

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
  single<ProtocolVersionUpdate>()

  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
  singleBy<ConnectivityVerifier, ConnectivityVerifierImpl>()

  factory<DefaultSettingsModel> { DefaultSettingsModelImpl }
  factory<ClientInformationModel> { ClientInformationModelImpl }
  factoryBy<MoveManager, MoveManagerImpl>()

  factory<SocketActivityChecker>()
  factory<RemoteBroadcastReceiver>()
  factory<SessionNotificationManager>()
  factory<RemoteSessionManager>()
  factory<RemoteVolumeProvider>()
  factory<LogHelper>()

  worker { QueueWorker(get(), get(), get()) }
  worker { SyncWorker(get(), get(), get(), get()) }
  fragment { LibraryFragment(get()) }
  fragment { LibraryAlbumTracksFragment(get(), get()) }
  fragment { LibraryArtistAlbumsFragment(get(), get()) }
  fragment { LibraryGenreArtistsFragment(get(), get()) }
}

val uiModule = module {
  viewModel<ConnectionManagerViewModel>()
  viewModel<PlayerViewModel>()
  viewModel<MiniControlViewModel>()
  viewModel<LyricsViewModel>()
  viewModel<RadioViewModel>()
  viewModel<NowPlayingViewModel>()
  viewModel<LibraryViewModel>()
  viewModel<PlaylistViewModel>()
  viewModel<FeedbackViewModel>()

  viewModel<VolumeDialogViewModel>()

  factory<RadioAdapter>()
  factory<GenreAdapter>()
  factory<ArtistAdapter>()
  factory<AlbumAdapter>()
  factory<TrackAdapter>()

  viewModel<AlbumViewModel> { AlbumViewModelImpl(get(), get(), get()) }
  viewModel<GenreViewModel> { GenreViewModelImpl(get(), get(), get()) }
  viewModel<ArtistViewModel> { ArtistViewModelImpl(get(), get(), get(), get()) }
  viewModel<TrackViewModel> { TrackViewModelImpl(get(), get(), get()) }

  single<LibrarySearchModel>()
  factory<GenreScreen>()
  factory<AlbumScreen>()
  factory<ArtistScreen>()
  factory<TrackScreen>()

  viewModel<GenreArtistViewModel> { GenreArtistViewModelImpl(get(), get(), get()) }
  viewModel<ArtistAlbumViewModel> { ArtistAlbumViewModelImpl(get(), get(), get()) }
  viewModel<AlbumTrackViewModel> { AlbumTrackViewModelImpl(get(), get(), get()) }
}
