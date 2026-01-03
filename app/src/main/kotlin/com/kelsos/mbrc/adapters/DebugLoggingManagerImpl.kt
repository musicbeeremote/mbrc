package com.kelsos.mbrc.adapters

import android.app.Application
import com.kelsos.mbrc.core.common.utilities.logging.FileLoggingTree
import com.kelsos.mbrc.feature.settings.DebugLoggingManager
import timber.log.Timber

class DebugLoggingManagerImpl(private val application: Application) : DebugLoggingManager {
  override fun setDebugLogging(enabled: Boolean) {
    if (enabled) {
      val hasFileLoggingTree = Timber.forest().any { it is FileLoggingTree }
      if (!hasFileLoggingTree) {
        Timber.plant(FileLoggingTree(application))
        Timber.d("Debug logging enabled")
      }
    } else {
      val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
      fileLoggingTree?.let {
        Timber.uproot(it)
        Timber.d("Debug logging disabled")
      }
    }
  }
}
