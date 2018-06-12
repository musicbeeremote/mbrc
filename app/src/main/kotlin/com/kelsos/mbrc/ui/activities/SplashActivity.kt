package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.scope

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    scope(application)
    NavigationActivity.start(this@SplashActivity)
  }
}
