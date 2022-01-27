package com.kelsos.mbrc.features.help

import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.logging.LogHelper

data class Feedback(
  val feedback: String,
  val includeLogs: Boolean,
  val includeDeviceInfo: Boolean,
)

typealias SendFeedback = suspend (feedback: Feedback) -> Unit

private val NavigationActivity.deviceInfo: String get() {
  val device = Build.DEVICE
  val manufacturer = Build.MANUFACTURER
  val appVersion = RemoteUtils.version
  val androidVersion = Build.VERSION.RELEASE

  return getString(
    R.string.feedback_version_info,
    manufacturer,
    device,
    androidVersion,
    appVersion,
  )
}

suspend fun NavigationActivity.sendFeedback(feedback: Feedback) {
  val logHelper = LogHelper()
  val feedbackText =
    if (feedback.includeDeviceInfo) {
      feedback.feedback + deviceInfo
    } else {
      feedback.feedback
    }

  val logFile =
    if (logHelper.logsExist(filesDir)) {
      logHelper.zipLogs(filesDir, externalCacheDir)
    } else {
      null
    }

  val emailIntent =
    Intent(Intent.ACTION_SEND).apply {
      putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
      type = "message/rfc822"
      putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
      putExtra(Intent.EXTRA_TEXT, feedbackText)
      logFile?.let { logs ->
        val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"
        val logsUri = FileProvider.getUriForFile(this@sendFeedback, authority, logs)
        putExtra(Intent.EXTRA_STREAM, logsUri)
      }
    }

  startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
}
