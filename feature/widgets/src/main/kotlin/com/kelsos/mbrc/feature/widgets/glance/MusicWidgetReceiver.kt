package com.kelsos.mbrc.feature.widgets.glance

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base widget receiver that rebuilds the widget after the app package is replaced.
 *
 * When the app is updated the launcher can keep the widget's previous RemoteViews,
 * whose click PendingIntent templates no longer carry the target intent Glance expects.
 * Tapping such a stale widget then crashes in `InvisibleActionTrampolineActivity` with
 * "List adapter activity trampoline invoked without specifying target intent.".
 *
 * [GlanceAppWidgetReceiver] already refreshes on `ACTION_LOCALE_CHANGED` but does nothing
 * for `ACTION_MY_PACKAGE_REPLACED`, so we handle it here and force [updateAll] to
 * regenerate the RemoteViews (and their PendingIntents) for every widget instance.
 */
abstract class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
      refreshAfterPackageReplaced(context)
    }
  }

  @Suppress("TooGenericExceptionCaught") // A widget refresh must never crash the receiver.
  private fun refreshAfterPackageReplaced(context: Context) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
      try {
        glanceAppWidget.updateAll(context)
      } catch (e: Exception) {
        Timber.e(e, "Failed to refresh widget after package replace")
      } finally {
        pendingResult.finish()
      }
    }
  }
}
