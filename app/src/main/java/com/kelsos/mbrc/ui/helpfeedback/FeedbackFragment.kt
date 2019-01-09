package com.kelsos.mbrc.ui.helpfeedback

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.BuildConfig.APPLICATION_ID
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentFeedbackBinding
import com.kelsos.mbrc.logging.LogHelper
import com.kelsos.mbrc.utilities.RemoteUtils.getVersion
import java.io.File

class FeedbackFragment : Fragment() {

  private var _binding: FragmentFeedbackBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentFeedbackBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    LogHelper.logsExist(requireContext()) { exists ->
      binding.includeLogInfo.isEnabled = exists
    }
    binding.feedbackButton.setOnClickListener { onFeedbackButtonClicked() }
  }

  private fun onFeedbackButtonClicked() {
    var feedbackText = binding.feedbackContent.text.toString().trim { it <= ' ' }
    if (TextUtils.isEmpty(feedbackText)) {
      return
    }

    binding.feedbackButton.isEnabled = false

    if (binding.includeDeviceInfo.isChecked) {
      val device = Build.DEVICE
      val manufacturer = Build.MANUFACTURER
      val appVersion = getVersion()
      val androidVersion = Build.VERSION.RELEASE

      feedbackText += getString(
        R.string.feedback_version_info,
        manufacturer,
        device,
        androidVersion,
        appVersion
      )
    }

    if (!binding.includeLogInfo.isChecked) {
      openChooser(feedbackText)
      return
    }

    LogHelper.zipLogs(requireContext()) { file ->
      openChooser(feedbackText, file)
    }
  }

  private fun openChooser(feedbackText: String, logs: File? = null) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackText)
    if (logs != null) {
      val authority = "$APPLICATION_ID.fileprovider"
      val logsUri = FileProvider.getUriForFile(requireContext(), authority, logs)
      emailIntent.putExtra(Intent.EXTRA_STREAM, logsUri)
    }

    binding.feedbackButton.isEnabled = true

    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
  }

  companion object {

    fun newInstance(): FeedbackFragment {
      return FeedbackFragment()
    }
  }
}
