package com.kelsos.mbrc.platform

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import toothpick.Toothpick
import javax.inject.Inject

/**
 * An [IntentService] subclass for handling the metadata sync operation
 */
class LibrarySyncService : IntentService("LibrarySyncService") {

  @Inject lateinit var librarySyncInteractor: LibrarySyncInteractor

  override fun onCreate() {
    val scope = Toothpick.openScopes(application, this)
    Toothpick.inject(this, scope)
    super.onCreate()
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

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
   * Handle action sync in the provided background thread with the provided
   * parameters.
   */
  private fun handleActionSync(auto: Boolean) {
    librarySyncInteractor.sync(auto)
  }

  companion object {
    /**
     * Sync action for the library data
     */
    private val ACTION_SYNC = "com.kelsos.mbrc.action.SYNC"

    /**
     * Who started the sync operation. This is to distinct between user initiated sync operations
     */
    private val EXTRA_AUTO = "com.kelsos.mbrc.dev.extra.AUTO"

    /**
     * Starts this service to perform a library metadata sync operatin with the given parameters. If
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