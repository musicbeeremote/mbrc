package com.kelsos.mbrc.ui.help_feedback

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber

class HelpFragment : Fragment() {

  @BindView(R.id.help_webview) lateinit var helpWebview: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onStart() {
    super.onStart()

    val url: String
    url = try {
      String.format("http://kelsos.net/musicbeeremote/help?version=%s", RemoteUtils.getVersion(context))
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "http://kelsos.net/musicbeeremote/help"
    }

    helpWebview.loadUrl(url)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater!!.inflate(R.layout.fragment_help, container, false)
    ButterKnife.bind(this, view)
    helpWebview.setWebViewClient(RemoteWebViewClient())
    return view
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
}// Required empty public constructor
