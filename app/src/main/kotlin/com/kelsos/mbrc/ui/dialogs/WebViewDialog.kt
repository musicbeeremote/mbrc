package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import com.kelsos.mbrc.extensions.fail

class WebViewDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = context ?: fail("null context")
    val builder = MaterialDialog.Builder(context)
    val webView = WebView(context)
    val arguments = arguments ?: fail("no arguments")
    webView.loadUrl(arguments.getString(ARG_URL))
    builder.customView(webView, false)
    builder.title(arguments.getInt(ARG_TITLE))
    builder.positiveText(android.R.string.ok)
    builder.onPositive { dialog, _ -> dialog.dismiss() }
    return builder.build()
  }

  companion object {

    val ARG_URL = "url"
    val ARG_TITLE = "title"
  }
}
