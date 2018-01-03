package com.kelsos.mbrc.ui.navigation.library

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R

@SuppressLint("InflateParams")
fun LibraryActivity.syncDialog(): SyncProgressDialog {
  return SyncProgressDialog.create(this)
}

class SyncProgressDialog(private val context: Context) {
  private val dialog: AlertDialog

  private val current: TextView
  private val progress: ProgressBar

  init {
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.dialog__syncing_library, null, false)

    current = view.findViewById(R.id.progress_dialog__current)
    progress = view.findViewById(R.id.progress_dialog__loading_progress_bar)

    dialog = AlertDialog.Builder(context)
        .setView(view)
        .setCancelable(false)
        .create()
  }

  fun show() {
    dialog.show()
  }

  fun dismiss() {
    dialog.dismiss()
  }

  @SuppressLint("SwitchIntDef")
  fun updateProgress(progress: SyncProgress) {
    val totalItems = progress.total
    val currentProgress = progress.current.coerceAtMost(totalItems)

    val loadingProgress = when (progress.type) {
      SyncProgress.GENRE -> context.getString(R.string.library_sync__genres_progress, currentProgress, totalItems)
      SyncProgress.ARTIST -> context.getString(R.string.library_sync__artists_progress, currentProgress, totalItems)
      SyncProgress.ALBUM -> context.getString(R.string.library_sync__albums_progress, currentProgress, totalItems)
      SyncProgress.TRACK -> context.getString(R.string.library_sync__tracks_progress, currentProgress, totalItems)
      else -> throw IllegalArgumentException("${progress.type} is not supported")
    }

    current.text = loadingProgress


    this.progress.max = totalItems
    this.progress.progress = currentProgress
  }

  companion object {
    fun create(context: Context): SyncProgressDialog {
      return SyncProgressDialog(context)
    }
  }
}
