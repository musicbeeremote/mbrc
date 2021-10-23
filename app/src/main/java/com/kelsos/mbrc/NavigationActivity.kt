package com.kelsos.mbrc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.firstOrNone
import com.kelsos.mbrc.app.RemoteApp
import com.kelsos.mbrc.common.ui.BaseFragment
import com.kelsos.mbrc.features.help.sendFeedback
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import kotlinx.coroutines.flow.collect
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinExperimentalAPI
import timber.log.Timber

class NavigationActivity : AppCompatActivity() {

  private val viewmodel: NavigationViewModel by viewModel()

  private fun share(track: PlayingTrack) {
    val shareIntent = Intent.createChooser(sendIntent(track), null)
    startActivity(shareIntent)
  }

  private fun sendIntent(track: PlayingTrack): Intent {
    return Intent(Intent.ACTION_SEND).apply {
      val payload = "Now Playing: ${track.artist} - ${track.title}"
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, payload)
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    var auto = true
    lifecycleScope.launchWhenStarted {
      viewmodel.connection.collect { status ->
        if (auto && status != ConnectionStatus.Active) {
          auto = false
          viewmodel.connect()
        }
      }
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        viewmodel.incrementVolume()
        true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        viewmodel.descrementVolume()
        true
      }
      else -> super.onKeyUp(keyCode, event)
    }
  }

  @OptIn(KoinExperimentalAPI::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    setupKoinFragmentFactory()
    super.onCreate(savedInstanceState)
    setContent {
      RemoteApp(viewmodel, sendFeedback = { sendFeedback(it) }) { share(it) }
    }
    viewmodel.startService()
  }

  override fun onResume() {
    super.onResume()
    if (!BuildConfig.DEBUG) {
      return
    }

    // don't even consider it otherwise
    if (Debug.isDebuggerConnected()) {
      Timber.d("Keeping screen on for debugging.")
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
      window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      Timber.d("Keeping screen on for debugging is now deactivated.")
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        viewmodel.incrementVolume()
        true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        viewmodel.descrementVolume()
        true
      }
      else -> super.onKeyDown(keyCode, event)
    }
  }

  override fun onBackPressed() {
    val fragments = supportFragmentManager.fragments

    fragments.filterIsInstance<BaseFragment>()
      .firstOrNone { fragment ->
        fragment.onBackPressed()
      }.fold({ super.onBackPressed() }, {})
  }

  companion object {
    fun start(context: Context) {
      with(context) {
        startActivity(Intent(this, NavigationActivity::class.java))
      }
    }
  }
}
