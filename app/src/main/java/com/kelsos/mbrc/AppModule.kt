package com.kelsos.mbrc

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.work.WorkManager
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.AppStateManager
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.data.Database.Companion.V1
import com.kelsos.mbrc.data.Database.Companion.V2
import com.kelsos.mbrc.data.Database.Companion.V3
import com.kelsos.mbrc.data.Database.Companion.V4
import com.kelsos.mbrc.data.DatabaseTransactionRunner
import com.kelsos.mbrc.data.DatabaseTransactionRunnerImpl
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.DeserializationAdapterImpl
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.data.SerializationAdapterImpl
import com.kelsos.mbrc.features.library.details.AlbumTrackViewModel
import com.kelsos.mbrc.features.library.details.ArtistAlbumViewModel
import com.kelsos.mbrc.features.library.details.GenreArtistViewModel
import com.kelsos.mbrc.features.library.presentation.AlbumViewModel
import com.kelsos.mbrc.features.library.presentation.ArtistViewModel
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.presentation.LibraryViewModel
import com.kelsos.mbrc.features.library.presentation.TrackViewModel
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.AlbumRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.CoverCache
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepositoryImpl
import com.kelsos.mbrc.features.library.repositories.LibraryRepositories
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
import com.kelsos.mbrc.features.playlists.PlaylistRepository
import com.kelsos.mbrc.features.playlists.PlaylistRepositoryImpl
import com.kelsos.mbrc.features.playlists.PlaylistViewModel
import com.kelsos.mbrc.features.queue.QueueUseCase
import com.kelsos.mbrc.features.queue.QueueUseCaseImpl
import com.kelsos.mbrc.features.queue.QueueWorker
import com.kelsos.mbrc.features.radio.RadioRepository
import com.kelsos.mbrc.features.radio.RadioRepositoryImpl
import com.kelsos.mbrc.features.radio.RadioViewModel
import com.kelsos.mbrc.features.settings.ClientInformationStore
import com.kelsos.mbrc.features.settings.ClientInformationStoreImpl
import com.kelsos.mbrc.features.settings.ConnectionManagerViewModel
import com.kelsos.mbrc.features.settings.DefaultActionPreferenceStore
import com.kelsos.mbrc.features.settings.PlayingTrackCache
import com.kelsos.mbrc.features.settings.PlayingTrackCacheImpl
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.features.settings.SettingsManagerImpl
import com.kelsos.mbrc.features.settings.SettingsViewModel
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
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.Executors

val appModule = module {
  single { Moshi.Builder().build() }
  singleOf(::QueueUseCaseImpl) { bind<QueueUseCase>() }

  singleOf(::ConnectionRepositoryImpl) { bind<ConnectionRepository>() }

  singleOf(::TrackRepositoryImpl) { bind<TrackRepository>() }
  singleOf(::AlbumRepositoryImpl) { bind<AlbumRepository>() }
  singleOf(::ArtistRepositoryImpl) { bind<ArtistRepository>() }
  singleOf(::GenreRepositoryImpl) { bind<GenreRepository>() }

  singleOf(::LibraryRepositories)

  singleOf(::NowPlayingRepositoryImpl) { bind<NowPlayingRepository>() }
  singleOf(::PlaylistRepositoryImpl) { bind<PlaylistRepository>() }
  singleOf(::CoverCache)
  singleOf(::SyncStatProvider)

  singleOf(::SerializationAdapterImpl) { bind<SerializationAdapter>() }
  singleOf(::DeserializationAdapterImpl) { bind<DeserializationAdapter>() }
  singleOf(::DatabaseTransactionRunnerImpl) { bind<DatabaseTransactionRunner>() }
  singleOf(::RequestManagerImpl) { bind<RequestManager>() }

  singleOf(::UserActionUseCaseImpl) { bind<UserActionUseCase>() }

  singleOf(::ClientConnectionUseCaseImpl) { bind<ClientConnectionUseCase>() }

  singleOf(::SettingsManagerImpl) { bind<SettingsManager>() }
  singleOf(::PlayingTrackCacheImpl) { bind<PlayingTrackCache>() }

  singleOf(::LibrarySyncUseCaseImpl) { bind<LibrarySyncUseCase>() }
  singleOf(::SyncWorkHandlerImpl) { bind<SyncWorkHandler>() }

  singleOf(::RadioRepositoryImpl) { bind<RadioRepository>() }
  singleOf(::ClientInformationStoreImpl) { bind<ClientInformationStore>() }
  singleOf(::VolumeModifyUseCaseImpl) { bind<VolumeModifyUseCase>() }
  singleOf(::OutputApiImpl) { bind<OutputApi>() }

  singleOf(::AppState)
  singleOf(::ConnectionState)

  singleOf(::MessageQueueImpl) { bind<MessageQueue>() }
  singleOf(::MessageHandlerImpl) { bind<MessageHandler>() }

  singleOf(::ClientConnectionManager) { bind<IClientConnectionManager>() }
  singleOf(::CommandFactoryImpl) { bind<CommandFactory>() }
  singleOf(::UiMessageQueueImpl) { bind<UiMessages>() }
  singleOf(::RemoteServiceDiscoveryImpl) { bind<RemoteServiceDiscovery>() }

  singleOf(::SessionNotificationManager) { bind<INotificationManager>() }
  singleOf(::ServiceCheckerImpl) { bind<ServiceChecker>() }
  singleOf(::AppStateManager)

  singleOf(::DefaultActionPreferenceStore)

  singleOf(::WidgetUpdaterImpl) { bind<WidgetUpdater>() }

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
  singleOf(::WorkHandlerImpl) { bind<WorkHandler>() }

  singleOf(::ApiBase)

  single {
    Room.databaseBuilder(get(), Database::class.java, "cache.db")
      .fallbackToDestructiveMigrationFrom(V1, V2, V3, V4).build()
  }
  single { get<Database>().genreDao() }
  single { get<Database>().artistDao() }
  single { get<Database>().albumDao() }
  single { get<Database>().trackDao() }
  single { get<Database>().nowPlayingDao() }
  single { get<Database>().playlistDao() }
  single { get<Database>().radioStationDao() }
  single { get<Database>().connectionDao() }

  singleOf(::UpdateNowPlayingTrack)
  singleOf(::UpdateCover)
  singleOf(::UpdateRating)
  singleOf(::UpdatePlayerStatus)
  singleOf(::UpdatePlayState)
  singleOf(::UpdateRepeat)
  singleOf(::UpdateVolume)
  singleOf(::UpdateMute)
  singleOf(::UpdateShuffle)
  singleOf(::UpdateLastFm)
  singleOf(::UpdateLyrics)
  singleOf(::UpdateLfmRating)
  singleOf(::UpdateNowPlayingTrackRemoval)
  singleOf(::UpdateNowPlayingTrackMoved)
  singleOf(::UpdatePlaybackPositionCommand)
  singleOf(::UpdatePluginVersionCommand)
  singleOf(::ProtocolPingHandle)
  singleOf(::SimpleLogCommand)
  singleOf(::ProtocolVersionUpdate)

  singleOf(::SocketActivityChecker)
  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
  singleOf(::ConnectivityVerifierImpl) { bind<ConnectivityVerifier>() }

  factoryOf(::MoveManagerImpl) { bind<MoveManager>() }

  factoryOf(::RemoteBroadcastReceiver)
  factoryOf(::RemoteSessionManager)
  factoryOf(::RemoteVolumeProvider)
  factoryOf(::LogHelper)

  workerOf(::QueueWorker)
  workerOf(::SyncWorker)
}

val uiModule = module {
  viewModelOf(::ConnectionManagerViewModel)
  viewModelOf(::PlayerViewModel)
  viewModelOf(::MiniControlViewModel)
  viewModelOf(::LyricsViewModel)
  viewModelOf(::RadioViewModel)
  viewModelOf(::NowPlayingViewModel)
  viewModelOf(::LibraryViewModel)
  viewModelOf(::PlaylistViewModel)
  viewModelOf(::OutputSelectionViewModel)
  viewModelOf(::RatingDialogViewModel)
  viewModelOf(::VolumeDialogViewModel)

  viewModelOf(::AlbumViewModel)
  viewModelOf(::GenreViewModel)
  viewModelOf(::ArtistViewModel)
  viewModelOf(::TrackViewModel)

  singleOf(::LibrarySearchModel)

  viewModelOf(::GenreArtistViewModel)
  viewModelOf(::ArtistAlbumViewModel)
  viewModelOf(::AlbumTrackViewModel)

  viewModelOf(::NavigationViewModel)
  viewModelOf(::SettingsViewModel)
}
