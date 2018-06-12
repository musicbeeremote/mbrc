package com.kelsos.mbrc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.databinding.ActivityNavigationBinding
import com.kelsos.mbrc.databinding.NavHeaderMainBinding
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.platform.ServiceChecker
import timber.log.Timber
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NavigationActivity : AppCompatActivity() {

  @Inject
  lateinit var serviceChecker: ServiceChecker

  @Inject
  lateinit var volumeInteractor: VolumeInteractor

  @Inject
  lateinit var connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider

  @Inject
  lateinit var clientConnectionUseCase: ClientConnectionUseCase

  private lateinit var binding: ActivityNavigationBinding

  private lateinit var connectText: TextView
  private lateinit var connect: ImageView

  private fun onConnectLongClick(): Boolean {
    serviceChecker.startServiceIfNotRunning()
    clientConnectionUseCase.connect()
    return true
  }

  private fun onConnectClick() {
    serviceChecker.startServiceIfNotRunning()
    clientConnectionUseCase.connect()
  }

  private fun onConnection(connectionStatus: ConnectionStatus) {
    Timber.v("Handling new connection status ${Connection.string(connectionStatus.status)}")

    @StringRes val resId: Int
    @ColorRes val colorId: Int
    when (connectionStatus.status) {
      Connection.OFF -> {
        resId = R.string.drawer_connection_status_off
        colorId = R.color.black
      }
      Connection.ON -> {
        resId = R.string.drawer_connection_status_on
        colorId = R.color.accent
      }
      Connection.ACTIVE -> {
        resId = R.string.drawer_connection_status_active
        colorId = R.color.power_on
      }
      else -> {
        resId = R.string.drawer_connection_status_off
        colorId = R.color.black
      }
    }

    connectText.setText(resId)
    connect.setColorFilter(ContextCompat.getColor(this, colorId))
  }

  private fun setupConnectionIndicator() {
    val header = binding.navView.getHeaderView(0)
    val binding = NavHeaderMainBinding.bind(header)
    connectText = binding.navConnectText
    connect = binding.connectButton.apply {
      setOnClickListener { onConnectClick() }
      setOnLongClickListener { onConnectLongClick() }
    }
  }

  private fun setupNavigation() {
    setSupportActionBar(binding.toolbar)
    val navHostFragment = supportFragmentManager.findFragmentById(
      R.id.main_navigation_fragment
    ) as NavHostFragment

    val navController = navHostFragment.navController
    setupWithNavController(binding.navView, navController)
    setupActionBarWithNavController(this, navController, binding.drawerLayout)
    setupNavigationMenu(navController)
  }

  private fun setupNavigationMenu(navController: NavController) {
    setupWithNavController(binding.navView, navController)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    connectionStatusLiveDataProvider.get().observe(
      this,
      Observer {
        if (it == null) {
          return@Observer
        }
        onConnection(it)
      }
    )

    if (connectionStatusLiveDataProvider.getValue()?.status != Connection.ACTIVE) {
      onConnectClick()
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> true
      KeyEvent.KEYCODE_VOLUME_DOWN -> true
      else -> super.onKeyUp(keyCode, event)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    return findNavController(R.id.main_navigation_fragment).navigateUp()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scopes = Toothpick.openScopes(application, this)
    scopes.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    binding = ActivityNavigationBinding.inflate(layoutInflater)
    setContentView(binding.root)
    scopes.inject(this)
    setupNavigation()
    setupConnectionIndicator()
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

  override fun onDestroy() {
    super.onDestroy()
    connectionStatusLiveDataProvider.get().removeObservers(this)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        volumeInteractor.increment()
        true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        volumeInteractor.decrement()
        true
      }
      else -> super.onKeyDown(keyCode, event)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Have the NavHelper look for an action or destination matching the menu
    // item id and navigate there if found.
    // Otherwise, bubble up to the parent.
    return onNavDestinationSelected(
      item,
      findNavController(
        this,
        R.id.main_navigation_fragment
      )
    ) || super.onOptionsItemSelected(item)
  }

  companion object {
    fun start(context: Context) {
      with(context) {
        startActivity(Intent(this, NavigationActivity::class.java))
      }
    }
  }
}
