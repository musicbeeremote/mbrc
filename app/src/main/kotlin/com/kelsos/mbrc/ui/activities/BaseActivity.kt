package com.kelsos.mbrc.ui.activities

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kelsos.mbrc.R
import kotterknife.bindView

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

  private val toolbar: Toolbar by bindView(R.id.toolbar)

  protected fun setupToolbar(title: String = "") {
    setSupportActionBar(toolbar)

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)

      if (title.isNotBlank()) {
        this.title = title
      }
    }
  }

  protected fun showSnackbar(@StringRes resId: Int) {
    com.google.android.material.snackbar.Snackbar.make(
      findViewById(android.R.id.content),
      resId,
      com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
    ).show()
  }
}