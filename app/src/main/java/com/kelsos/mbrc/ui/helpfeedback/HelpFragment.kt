package com.kelsos.mbrc.ui.helpfeedback

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.RemoteUtils.getVersion
import com.kelsos.mbrc.databinding.FragmentHelpBinding
import timber.log.Timber

class HelpFragment : Fragment() {
  private lateinit var binding: FragmentHelpBinding

  override fun onStart() {
    super.onStart()

    val url: String
    url = try {
      "https://mbrc.kelsos.net/help?version=${getVersion()}"
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "https://mbrc.kelsos.net/help"
    }

    binding.helpWebview.loadUrl(url)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.helpWebview.webViewClient = RemoteWebViewClient()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.unbind()
  }

  private class RemoteWebViewClient : WebViewClient() {
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
      view.loadUrl(url)
      return false
    }
  }

  companion object {
    fun newInstance(): HelpFragment {
      return HelpFragment()
    }
  }
}
