package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.services.api.ApiService
import com.kelsos.mbrc.utilities.LibrarySyncManager
import timber.log.Timber

class LibrarySyncInteractorImpl
@Inject
constructor() : LibrarySyncInteractor {

  @Inject private lateinit var manager: LibrarySyncManager
  @Inject private lateinit var service: ApiService

  override fun sync() {
    service.getStatus().io().subscribe({
      if (it.code.equals(Code.SUCCESS)) {
        manager.sync()
      }
    }) {
      Timber.v(it, "Failed to start automatic sync")
    }
  }
}
