package com.kelsos.mbrc.platform

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

/**
 * An [IntentService] subclass for handling the metadata network operation
 */
class LibrarySyncService : IntentService("LibrarySyncService") {

  private val librarySyncUseCase: LibrarySyncUseCase by inject()

  override fun onHandleIntent(intent: Intent?) {
    if (intent != null) {
      val action = intent.action
      if (ACTION_SYNC == action) {
        val auto = intent.getBooleanExtra(EXTRA_AUTO, true)
        handleActionSync(auto)
      }
    }
  }

  /**
   * Handle action network in the provided background thread with the provided
   * parameters.
   */
  private fun handleActionSync(auto: Boolean) {
    runBlocking {
      librarySyncUseCase.sync(auto)
    }
  }

  companion object {
    /**
     * Sync action for the library data
     */
    private const val ACTION_SYNC = "com.kelsos.mbrc.action.SYNC"

    /**
     * Who started the network operation. This is to distinct between user initiated network operations
     */
    private const val EXTRA_AUTO = "com.kelsos.mbrc.dev.extra.AUTO"

    /**
     * Starts this service to perform a library metadata network operatin with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @see IntentService
     */
    fun startActionSync(context: Context, auto: Boolean) {
      val intent = Intent(context, LibrarySyncService::class.java)
      intent.action = ACTION_SYNC
      intent.putExtra(EXTRA_AUTO, auto)
      context.startService(intent)
    }
  }
}