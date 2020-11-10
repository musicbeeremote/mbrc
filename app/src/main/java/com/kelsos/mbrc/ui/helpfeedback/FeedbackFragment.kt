package com.kelsos.mbrc.ui.helpfeedback

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.logging.LogHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import java.io.File

class FeedbackFragment : Fragment() {

  private val feedbackEditText: EditText by bindView(R.id.feedback_content)
  private val deviceInfo: CheckBox by bindView(R.id.include_device_info)
  private val logInfo: CheckBox by bindView(R.id.include_log_info)
  private val feedbackButton: Button by bindView(R.id.feedback_button)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_feedback, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val context = context ?: error("null context")

    LogHelper.logsExist(context)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        logInfo.isEnabled = true
      }) {
      }
    feedbackButton.setOnClickListener { onFeedbackButtonClicked() }
  }

  private fun onFeedbackButtonClicked() {
    val context = context ?: error("null context")
    var feedbackText = feedbackEditText.text.toString().trim { it <= ' ' }
    if (TextUtils.isEmpty(feedbackText)) {
      return
    }

    feedbackButton.isEnabled = false

    if (deviceInfo.isChecked) {
      val device = Build.DEVICE
      val manufacturer = Build.MANUFACTURER
      val appVersion = RemoteUtils.getVersion()
      val androidVersion = Build.VERSION.RELEASE

      feedbackText += getString(
        R.string.feedback_version_info,
        manufacturer,
        device,
        androidVersion,
        appVersion
      )
    }

    if (!logInfo.isChecked) {
      openChooser(feedbackText)
      return
    }

    LogHelper.zipLogs(context)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        openChooser(feedbackText, it)
      }) {
        openChooser(feedbackText)
      }
  }

  private fun openChooser(feedbackText: String, logs: File? = null) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText)
    if (logs != null) {
      val logsUri = Uri.fromFile(logs)
      emailIntent.putExtra(Intent.EXTRA_STREAM, logsUri)
    }

    feedbackButton.isEnabled = true

    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
  }

  companion object {

    fun newInstance(): FeedbackFragment {
      return FeedbackFragment()
    }
  }
}