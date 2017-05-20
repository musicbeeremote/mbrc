package com.kelsos.mbrc.di.modules

import androidx.core.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.features.output.OutputApi
import com.kelsos.mbrc.features.output.OutputApiImpl
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.RequestManagerImpl
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.AlbumRepositoryImpl
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.ArtistRepositoryImpl
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.GenreRepositoryImpl
import com.kelsos.mbrc.repository.ModelCache
import com.kelsos.mbrc.repository.ModelCacheImpl
import com.kelsos.mbrc.repository.NowPlayingRepository
import com.kelsos.mbrc.repository.NowPlayingRepositoryImpl
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.PlaylistRepositoryImpl
import com.kelsos.mbrc.repository.RadioRepository
import com.kelsos.mbrc.repository.RadioRepositoryImpl
import com.kelsos.mbrc.repository.TrackRepository
import com.kelsos.mbrc.repository.TrackRepositoryImpl
import com.kelsos.mbrc.repository.data.LocalArtistDataSource
import com.kelsos.mbrc.repository.data.LocalArtistDataSourceImpl
import com.kelsos.mbrc.repository.data.LocalRadioDataSource
import com.kelsos.mbrc.repository.data.RemoteRadioDataSource
import com.kelsos.mbrc.services.ServiceChecker
import com.kelsos.mbrc.services.ServiceCheckerImpl
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractorImpl
import com.kelsos.mbrc.utilities.SettingsManager
import com.kelsos.mbrc.utilities.SettingsManagerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import toothpick.config.Binding
import toothpick.config.Module
import java.util.concurrent.Executors
import kotlin.reflect.KClass

class RemoteModule : Module() {
  init {
    val mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())

    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()
    bind(ObjectMapper::class.java).toInstance(mapper)
    bind(NotificationManagerCompat::class.java).toProvider(
      NotificationManagerCompatProvider::class.java
    )
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)

    bind(TrackRepository::class.java).to(TrackRepositoryImpl::class.java)
    bind(AlbumRepository::class.java).to(AlbumRepositoryImpl::class.java)
    bind(ArtistRepository::class.java).to(ArtistRepositoryImpl::class.java)
    bind(GenreRepository::class.java).to(GenreRepositoryImpl::class.java)

    bind(LocalArtistDataSource::class.java).to(LocalArtistDataSourceImpl::class.java)

    bind(NowPlayingRepository::class.java).to(NowPlayingRepositoryImpl::class.java)
    bind(PlaylistRepository::class.java).to(PlaylistRepositoryImpl::class.java)

    bind(SettingsManager::class.java).to(SettingsManagerImpl::class.java).singletonInScope()
    bind(ModelCache::class.java).to(ModelCacheImpl::class.java).singletonInScope()
    bind(ServiceChecker::class.java).to(ServiceCheckerImpl::class.java).singletonInScope()

    bind(LibrarySyncInteractor::class.java).to(LibrarySyncInteractorImpl::class.java)
      .singletonInScope()
    bind(ApiBase::class.java).singletonInScope()
    bind(RequestManager::class.java).to(RequestManagerImpl::class.java).singletonInScope()
    bind(OutputApi::class.java).to(OutputApiImpl::class.java).singletonInScope()
    bind(AppDispatchers::class.java).toInstance(
      AppDispatchers(
        Dispatchers.Main,
        Dispatchers.IO,
        Executors.newSingleThreadExecutor { Thread(it, "db") }.asCoroutineDispatcher()
      )
    )
    bind(QueueHandler::class.java).singletonInScope()

    bind(LocalRadioDataSource::class.java).to(LocalRadioDataSource::class.java).singletonInScope()
    bind(RemoteRadioDataSource::class.java).to(RemoteRadioDataSource::class.java).singletonInScope()
    bind(RadioRepository::class.java).to(RadioRepositoryImpl::class.java).singletonInScope()
  }
}

fun <T : Any> Module.bind(clazz: KClass<T>): Binding<T> = bind(clazz.java)

fun <T : Any, K : T> Binding<T>.to(
  clazz: KClass<K>
): Binding<T>.BoundStateForClassBinding = this.to(clazz.java)
