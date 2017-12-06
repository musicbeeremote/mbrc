package com.kelsos.mbrc.ui.helpfeedback

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient


import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.fail
import com.kelsos.mbrc.utilities.RemoteUtils
import kotterknife.bindView
import timber.log.Timber

class HelpFragment : Fragment() {

  private val helpWebview: WebView by bindView(R.id.help_webview)

  override fun onStart() {
    super.onStart()

    val url: String
    url = try {
      val context = context ?: fail("null context")
      String.format("http://kelsos.net/musicbeeremote/help?version=%s", RemoteUtils.getVersion(context))
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "http://kelsos.net/musicbeeremote/help"
    }

    helpWebview.loadUrl(url)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
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
