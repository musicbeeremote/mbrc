package com.kelsos.mbrc.ui.helpfeedback

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.databinding.FragmentFeedbackBinding
import org.koin.android.ext.android.inject
import java.io.File

class FeedbackFragment : Fragment() {
  private lateinit var binding: FragmentFeedbackBinding
  private lateinit var includeDevice: CheckBox
  private lateinit var includeLogs: CheckBox
  private lateinit var feedbackInput: TextInputEditText
  private lateinit var sendButton: Button

  private val viewModel: FeedbackViewModel by inject()

  private val feedbackText: String
    get() {
      var feedbackText = feedbackInput.text.toString().trim { it <= ' ' }
      if (includeDevice.isChecked) {
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
      return feedbackText
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
    binding.feedbackSend.setOnClickListener { onFeedbackButtonClicked() }
    feedbackInput = binding.feedbackContent
    includeDevice = binding.feedbackIncludeDevice
    includeLogs = binding.feedbackIncludeLogs
    sendButton = binding.feedbackSend
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.emitter.observe(viewLifecycleOwner) {
      if (it.hasBeenHandled) {
        return@observe
      }

      val message = it.contentIfNotHandled ?: return@observe
      when (message) {
        is FeedbackUiMessage.ZipFileCreated -> openChooser(feedbackText, message.zip)
        is FeedbackUiMessage.SendFeedback -> openChooser(feedbackText)
        is FeedbackUiMessage.EnableLogs -> includeLogs.isEnabled = true
      }
    }
    viewModel.checkForLogs(requireContext().filesDir)
  }

  private fun onFeedbackButtonClicked() {
    if (feedbackText.isEmpty()) {
      return
    }

    sendButton.isEnabled = false
    if (!includeLogs.isChecked) {
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
        val logsUri = Uri.fromFile(logs)
        putExtra(Intent.EXTRA_STREAM, logsUri)
      }
    }

    sendButton.isEnabled = true

    startActivity(Intent.createChooser(emailIntent, getString(R.string.feedback_chooser_title)))
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.unbind()
  }

  companion object {

    fun newInstance(): FeedbackFragment {
      return FeedbackFragment()
    }
  }
}
