package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.withContext

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    async(CommonPool) {
      scope(application)

      delay(800)

      withContext(UI) {
        MainActivity.start(this@SplashActivity)
      }
    }
  }
}