package com.kelsos.mbrc.ui.help_feedback

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
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
      String.format("https://mbrc.kelsos.net/help?version=%s", RemoteUtils.getVersion(requireContext()))
    } catch (e: PackageManager.NameNotFoundException) {
      Timber.v(e, "Failed to get version")
      "https://mbrc.kelsos.net/help"
    }

    helpWebview.loadUrl(url)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_help, container, false)
    ButterKnife.bind(this, view)
    helpWebview.webViewClient = RemoteWebViewClient()
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
