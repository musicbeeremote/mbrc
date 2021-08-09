package com.kelsos.mbrc

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.work.WorkManager
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.AppStateManager
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.PlayingTrackCacheImpl
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.data.DatabaseTransactionRunner
import com.kelsos.mbrc.data.DatabaseTransactionRunnerImpl
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.DeserializationAdapterImpl
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.data.SerializationAdapterImpl
import com.kelsos.mbrc.features.library.PopupActionHandler
import com.kelsos.mbrc.features.library.presentation.AlbumAdapter
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.library.presentation.ArtistAdapter
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.library.presentation.GenreAdapter
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.library.presentation.LibraryFragment
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.presentation.LibraryViewModel
import com.kelsos.mbrc.features.library.presentation.TrackAdapter
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.library.presentation.details.LibraryAlbumTracksFragment
import com.kelsos.mbrc.features.library.presentation.details.LibraryArtistAlbumsFragment
import com.kelsos.mbrc.features.library.presentation.details.LibraryGenreArtistsFragment
import com.kelsos.mbrc.features.library.presentation.details.viemodels.AlbumTrackViewModel
import com.kelsos.mbrc.features.library.presentation.details.viemodels.ArtistAlbumViewModel
import com.kelsos.mbrc.features.library.presentation.details.viemodels.GenreArtistViewModel
import com.kelsos.mbrc.features.library.presentation.screens.AlbumScreen
import com.kelsos.mbrc.features.library.presentation.screens.ArtistScreen
import com.kelsos.mbrc.features.library.presentation.screens.GenreScreen
import com.kelsos.mbrc.features.library.presentation.screens.TrackScreen
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.AlbumRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.CoverCache
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
import com.kelsos.mbrc.features.lyrics.LyricsViewModel
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.features.nowplaying.domain.MoveManager
import com.kelsos.mbrc.features.nowplaying.domain.MoveManagerImpl
import com.kelsos.mbrc.features.nowplaying.presentation.NowPlayingViewModel
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.features.output.OutputApi
import com.kelsos.mbrc.features.output.OutputApiImpl
import com.kelsos.mbrc.features.output.OutputSelectionViewModel
import com.kelsos.mbrc.features.player.PlayerViewModel
import com.kelsos.mbrc.features.player.RatingDialogViewModel
import com.kelsos.mbrc.features.player.VolumeDialogViewModel
import com.kelsos.mbrc.features.playlists.presentation.PlaylistAdapter
import com.kelsos.mbrc.features.playlists.presentation.PlaylistViewModel
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.features.queue.QueueUseCase
import com.kelsos.mbrc.features.queue.QueueUseCaseImpl
import com.kelsos.mbrc.features.queue.QueueWorker
import com.kelsos.mbrc.features.radio.RadioRepository
import com.kelsos.mbrc.features.radio.RadioRepositoryImpl
import com.kelsos.mbrc.features.radio.RadioViewModel
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
import com.kelsos.mbrc.networking.client.UiMessageQueueImpl
import com.kelsos.mbrc.networking.client.UiMessages
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.UserActionUseCaseImpl
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscoveryImpl
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
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.preferences.SettingsManagerImpl
import com.kelsos.mbrc.protocol.ProtocolPingHandle
import com.kelsos.mbrc.protocol.ProtocolVersionUpdate
import com.kelsos.mbrc.protocol.SimpleLogCommand
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
import com.kelsos.mbrc.ui.helpfeedback.FeedbackViewModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.bind
import org.koin.dsl.factory
import org.koin.dsl.module
import org.koin.dsl.single
import java.util.concurrent.Executors

val appModule = module {
  single { Moshi.Builder().build() }
  single<QueueUseCaseImpl>() bind QueueUseCase::class

  single<ConnectionRepositoryImpl>() bind ConnectionRepository::class

  single<TrackRepositoryImpl>() bind TrackRepository::class
  single<AlbumRepositoryImpl>() bind AlbumRepository::class
  single<ArtistRepositoryImpl>() bind ArtistRepository::class
  single<GenreRepositoryImpl>() bind GenreRepository::class

  single<NowPlayingRepositoryImpl>() bind NowPlayingRepository::class
  single<PlaylistRepositoryImpl>() bind PlaylistRepository::class
  single<CoverCache>()
  single<SyncStatProvider>()

  single<SerializationAdapterImpl>() bind SerializationAdapter::class
  single<DeserializationAdapterImpl>() bind DeserializationAdapter::class
  single<DatabaseTransactionRunnerImpl>() bind DatabaseTransactionRunner::class
  single<RequestManagerImpl>() bind RequestManager::class

  single<UserActionUseCaseImpl>() bind UserActionUseCase::class

  single<ClientConnectionUseCaseImpl>() bind ClientConnectionUseCase::class

  single<SettingsManagerImpl>() bind SettingsManager::class
  single<PlayingTrackCacheImpl>() bind PlayingTrackCache::class

  single<LibrarySyncUseCaseImpl>() bind LibrarySyncUseCase::class
  single<SyncWorkHandlerImpl>() bind SyncWorkHandler::class

  single<RadioRepositoryImpl>() bind RadioRepository::class
  single<ClientInformationStoreImpl>() bind ClientInformationStore::class
  single<VolumeModifyUseCaseImpl>() bind VolumeModifyUseCase::class
  single<OutputApiImpl>() bind OutputApi::class

  single<AppState>()
  single<ConnectionState>()

  single<MessageQueueImpl>() bind MessageQueue::class
  single<MessageHandlerImpl>() bind MessageHandler::class

  single<ClientConnectionManager>() bind IClientConnectionManager::class
  single<CommandFactoryImpl>() bind CommandFactory::class
  single<UiMessageQueueImpl>() bind UiMessages::class
  single<RemoteServiceDiscoveryImpl>() bind RemoteServiceDiscovery::class

  single<SessionNotificationManager>() bind INotificationManager::class
  single<ServiceCheckerImpl>() bind ServiceChecker::class
  single<AppStateManager>()

  single<PopupActionHandler>()
  single<DefaultActionPreferenceStore>()

  single<WidgetUpdaterImpl>() bind WidgetUpdater::class

  single {
    val database = Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "DatabaseDispatcher")
    }.asCoroutineDispatcher()
    var threadId = 1
    val network = Executors.newFixedThreadPool(2) { runnable ->
      Thread(runnable, "NetworkDispatcher-worker-${threadId++}")
    }.asCoroutineDispatcher()
    AppCoroutineDispatchers(
      main = Dispatchers.Main,
      io = Dispatchers.IO,
      database = database,
      network = network
    )
  }

  single {
    WorkManager.getInstance(get())
  }
  single<WorkHandlerImpl>() bind WorkHandler::class

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
  single<SimpleLogCommand>()
  single<ProtocolVersionUpdate>()

  single<SocketActivityChecker>()
  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
  single<ConnectivityVerifierImpl>() bind ConnectivityVerifier::class

  factory<ClientInformationModelImpl>() bind ClientInformationModel::class
  factory<MoveManagerImpl>() bind MoveManager::class

  factory<RemoteBroadcastReceiver>()
  factory<RemoteSessionManager>()
  factory<RemoteVolumeProvider>()
  factory<LogHelper>()

  worker { QueueWorker(get(), get(), get()) }
  worker { SyncWorker(get(), get(), get(), get()) }
  fragment { LibraryFragment(get()) }
  fragment { LibraryAlbumTracksFragment(get(), get(), get()) }
  fragment { LibraryArtistAlbumsFragment(get(), get(), get()) }
  fragment { LibraryGenreArtistsFragment(get(), get(), get()) }
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
  viewModel<OutputSelectionViewModel>()
  viewModel<RatingDialogViewModel>()
  viewModel<VolumeDialogViewModel>()

  factory<PlaylistAdapter>()
  factory<GenreAdapter>()
  factory<ArtistAdapter>()
  factory<AlbumAdapter>()
  factory<TrackAdapter>()
  factory<ConnectionAdapter>()

  viewModel<AlbumViewModel>()
  viewModel<GenreViewModel>()
  viewModel<ArtistViewModel>()
  viewModel<TrackViewModel>()

  single<LibrarySearchModel>()
  factory<GenreScreen>()
  factory<AlbumScreen>()
  factory<ArtistScreen>()
  factory<TrackScreen>()

  viewModel<GenreArtistViewModel>()
  viewModel<ArtistAlbumViewModel>()
  viewModel<AlbumTrackViewModel>()

  viewModel<NavigationViewModel>()
}
