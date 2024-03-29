package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

  private val dispatchers: AppCoroutineDispatchers by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    lifecycleScope.launch(dispatchers.io) {
      delay(START_DELAY_MS)

      withContext(dispatchers.main) {
        NavigationActivity.start(this@SplashActivity)
      }
    }
  }

  companion object {
    private const val START_DELAY_MS = 800L
  }
}
