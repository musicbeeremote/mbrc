package com.kelsos.mbrc.ui.helpfeedback

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kelsos.mbrc.R
import com.kelsos.mbrc.utilities.RemoteUtils.getVersion
import kotterknife.bindView
import timber.log.Timber

class HelpFragment : androidx.fragment.app.Fragment() {

  private val helpWebview: WebView by bindView(R.id.help_webview)

  override fun onStart() {
    super.onStart()

    val url: String
    url = try {
      "http://kelsos.net/musicbeeremote/help?version=${getVersion()}"
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "http://kelsos.net/musicbeeremote/help"
    }

    helpWebview.loadUrl(url)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_help, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    helpWebview.webViewClient = RemoteWebViewClient()
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