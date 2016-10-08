package com.kelsos.mbrc.di.modules

import android.support.v4.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kelsos.mbrc.di.providers.NotificationManagerCompatProvider
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.bus.RxBusImpl
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.repository.ConnectionRepositoryImpl
import com.kelsos.mbrc.services.*
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import toothpick.config.Module

class RemoteModule : Module() {
  init {
    val mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())

    bind(RxBus::class.java).to(RxBusImpl::class.java).singletonInScope()
    bind(ObjectMapper::class.java).toInstance(mapper)
    bind(LibraryService::class.java).to(LibraryServiceImpl::class.java).singletonInScope()
    bind(PlaylistService::class.java).to(PlaylistServiceImpl::class.java).singletonInScope()
    bind(NowPlayingService::class.java).to(NowPlayingServiceImpl::class.java).singletonInScope()
    bind(NotificationManagerCompat::class.java).toProvider(NotificationManagerCompatProvider::class.java)
    bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
    bind(Scheduler::class.java).withName("main").toProviderInstance { AndroidSchedulers.mainThread() }
  }
}
