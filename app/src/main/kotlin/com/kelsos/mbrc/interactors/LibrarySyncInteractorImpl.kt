package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.services.api.ApiService
import com.kelsos.mbrc.utilities.LibrarySyncManager
import timber.log.Timber
import javax.inject.Inject

class LibrarySyncInteractorImpl
@Inject
constructor(private val manager: LibrarySyncManager,
            private val service: ApiService) : LibrarySyncInteractor {

  override fun sync() {
    service.getStatus().io().subscribe({
      if (it.code == Code.SUCCESS) {
        manager.sync()
      }
    }) {
      Timber.v(it, "Failed to start automatic sync")
    }
  }
}
