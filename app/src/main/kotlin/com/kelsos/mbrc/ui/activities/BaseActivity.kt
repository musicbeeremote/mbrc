package com.kelsos.mbrc.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.kelsos.mbrc.R

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

  protected fun setupToolbar(title: String = "", subtitle: String = "") {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)

      if (title.isNotBlank()) {
        this.title = title
      }

      this.subtitle = subtitle
    }
  }
}
