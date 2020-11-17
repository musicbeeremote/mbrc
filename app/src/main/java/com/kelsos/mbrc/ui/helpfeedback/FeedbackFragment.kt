package com.kelsos.mbrc.ui.helpfeedback

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.BuildConfig.APPLICATION_ID
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils.getVersion
import com.kelsos.mbrc.databinding.FragmentFeedbackBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class FeedbackFragment : Fragment() {

  private var _binding: FragmentFeedbackBinding? = null
  private val binding get() = _binding!!

  private val viewModel: FeedbackViewModel by viewModel()

  private val feedbackText: String
    get() {
      var feedbackText = binding.feedbackContent.text.toString().trim { it <= ' ' }
      if (binding.feedbackIncludeDevice.isChecked) {
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
      return feedbackText
    }

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
    binding.feedbackSend.setOnClickListener { onFeedbackButtonClicked() }
    lifecycleScope.launch {
      viewModel.emitter.collect { message ->
        when (message) {
          is FeedbackUiMessage.ZipFileCreated -> openChooser(feedbackText, message.zip)
          is FeedbackUiMessage.SendFeedback -> openChooser(feedbackText)
          is FeedbackUiMessage.EnableLogs -> binding.feedbackIncludeLogs.isEnabled = true
        }
      }
    }
    viewModel.checkForLogs(requireContext().filesDir)
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun onFeedbackButtonClicked() {
    if (feedbackText.isEmpty()) {
      return
    }

    binding.feedbackSend.isEnabled = false
    if (!binding.feedbackIncludeLogs.isChecked) {
      openChooser(feedbackText)
      return
    }

    viewModel.zipLogs(requireContext().filesDir, requireContext().externalCacheDir)
  }

  private fun openChooser(feedbackText: String, logs: File? = null) {
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
      putExtra(Intent.EXTRA_EMAIL, arrayOf("kelsos@kelsos.net"))
      type = "message/rfc822"
      putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
      putExtra(Intent.EXTRA_TEXT, feedbackText)
      logs?.let { logs ->
        val authority = "$APPLICATION_ID.fileprovider"
        val logsUri = FileProvider.getUriForFile(requireContext(), authority, logs)
        putExtra(Intent.EXTRA_STREAM, logsUri)
      }
    }

    binding.feedbackSend.isEnabled = true

    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
  }

  companion object {

    fun newInstance(): FeedbackFragment {
      return FeedbackFragment()
    }
  }
}
