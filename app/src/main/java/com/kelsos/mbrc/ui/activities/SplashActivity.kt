package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.AppDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

  private val dispatchers: AppDispatchers by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    GlobalScope.launch(dispatchers.io) {
      delay(800)

      withContext(dispatchers.main) {
        NavigationActivity.start(this@SplashActivity)
      }
    }
  }
}
