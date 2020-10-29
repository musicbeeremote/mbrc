package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog

class WebViewDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val activity = requireActivity()
    val builder = MaterialDialog.Builder(activity)
    val webView = WebView(activity)
    webView.loadUrl(arguments?.getString(ARG_URL) ?: throw Exception("argument null"))
    builder.customView(webView, false)
    builder.title(arguments?.getInt(ARG_TITLE) ?: throw Exception("argument null"))
    builder.positiveText(android.R.string.ok)
    builder.onPositive { dialog, _ -> dialog.dismiss() }
    return builder.build()
  }

  companion object {

    val ARG_URL = "url"
    val ARG_TITLE = "title"
  }
}
