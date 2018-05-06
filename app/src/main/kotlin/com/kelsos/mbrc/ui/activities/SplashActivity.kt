package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    runBlocking {
      scope(application)

      delay(800)

      withContext(Dispatchers.Main) {
        MainActivity.start(this@SplashActivity)
      }
    }
  }
}
