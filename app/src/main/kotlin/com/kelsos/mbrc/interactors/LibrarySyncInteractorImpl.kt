package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.services.api.TrackService
import com.kelsos.mbrc.utilities.LibrarySyncManager

class LibrarySyncInteractorImpl
@Inject
constructor() : LibrarySyncInteractor {

  @Inject private lateinit var manager:LibrarySyncManager
  @Inject private lateinit var service: TrackService

  override fun sync() {
    service.getTrackInfo().io().subscribe {
      manager.sync()
    }
  }
}
