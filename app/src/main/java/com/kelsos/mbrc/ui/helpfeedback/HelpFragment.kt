package com.kelsos.mbrc.ui.helpfeedback

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.common.utilities.RemoteUtils.getVersion
import com.kelsos.mbrc.databinding.FragmentHelpBinding
import timber.log.Timber

class HelpFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = FragmentHelpBinding.inflate(
      layoutInflater,
      container,
      false
    )

    val url = try {
      "https://mbrc.kelsos.net/help?version=${getVersion()}"
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "https://mbrc.kelsos.net/help"
    }

    binding.helpWebview.webViewClient = RemoteWebViewClient()
    binding.helpWebview.loadUrl(url)
    return binding.root
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
