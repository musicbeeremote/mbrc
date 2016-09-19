package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.services.LibraryService
import com.kelsos.mbrc.services.LibraryServiceImpl
import com.kelsos.mbrc.services.NowPlayingService
import com.kelsos.mbrc.services.NowPlayingServiceImpl
import toothpick.config.Module

class RemoteModule : Module() {
  init {
    bind(RxBus::class.java).toInstance(RxBusImpl())
    bind(ObjectMapper::class.java).toInstance(ObjectMapper())
    bind(LibraryService::class.java).to(LibraryServiceImpl::class.java)
    bind(NowPlayingService::class.java).to(NowPlayingServiceImpl::class.java)
    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java)
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
  }
}
