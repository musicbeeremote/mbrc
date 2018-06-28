package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    launch(CommonPool) {
      delay(800)

      withContext(UI) {
        NavigationActivity.start(this@SplashActivity)
      }
    }
  }
}