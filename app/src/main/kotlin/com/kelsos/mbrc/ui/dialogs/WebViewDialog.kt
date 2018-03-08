package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import com.kelsos.mbrc.extensions.fail

class WebViewDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = context ?: fail("null context")
    val builder = AlertDialog.Builder(context)
    val webView = WebView(context)
    val arguments = arguments ?: fail("no arguments")
    webView.loadUrl(arguments.getString(ARG_URL))
    builder.setView(webView)
    builder.setTitle(arguments.getInt(ARG_TITLE))
    builder.setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
    return builder.create()
  }

  companion object {

    val ARG_URL = "url"
    val ARG_TITLE = "title"
  }
}