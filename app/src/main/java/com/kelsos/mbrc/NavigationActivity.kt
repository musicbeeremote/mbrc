package com.kelsos.mbrc

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Debug
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import arrow.core.firstOrNone
import com.kelsos.mbrc.common.ui.BaseFragment
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusState
import com.kelsos.mbrc.databinding.ActivityNavigationBinding
import com.kelsos.mbrc.databinding.NavHeaderMainBinding
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCase
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.core.KoinExperimentalAPI
import timber.log.Timber

class NavigationActivity : AppCompatActivity() {
  private val volumeModifyUseCase: VolumeModifyUseCase by inject()
  private val connectionStatusLiveDataProvider: ConnectionStatusState by inject()
  private val clientConnectionUseCase: ClientConnectionUseCase by inject()

  private lateinit var binding: ActivityNavigationBinding

  private lateinit var connectText: TextView
  private lateinit var connect: ImageView
  private lateinit var drawerToggle: ActionBarDrawerToggle

  private fun onConnectLongClick(): Boolean {
    clientConnectionUseCase.connect()
    return true
  }

  private fun onConnectClick() {
    clientConnectionUseCase.connect()
  }

  private val onNavigatedListener: NavController.OnDestinationChangedListener =
    NavController.OnDestinationChangedListener { _, destination, _ ->
      supportActionBar?.title = destination.label
      val destinationId = destination.id

      Timber.v("dest: $destinationId ${destination.label}")

      val displayHome = when (destinationId) {
        R.id.settings_fragment,
        R.id.help_fragment,
        R.id.connection_manager_fragment,
        R.id.genre_artists_fragment,
        R.id.artist_albums_fragment,
        R.id.album_tracks_fragment -> false
        else -> true
      }

      drawerToggle.run {
        syncState()
        isDrawerIndicatorEnabled = displayHome
      }

      val lockMode = if (!displayHome) {
        DrawerLayout.LOCK_MODE_LOCKED_CLOSED
      } else {
        DrawerLayout.LOCK_MODE_UNLOCKED
      }
      binding.drawerLayout.setDrawerLockMode(lockMode)
    }

  private fun onConnection(connectionStatus: ConnectionStatus) {
    Timber.v("Handling new connection status $connectionStatus")

    @StringRes val resId: Int
    @ColorRes val colorId: Int
    when (connectionStatus) {
      ConnectionStatus.Off -> {
        resId = R.string.drawer_connection_status_off
        colorId = R.color.black
      }
      ConnectionStatus.On -> {
        resId = R.string.drawer_connection_status_on
        colorId = R.color.accent
      }
      ConnectionStatus.Active -> {
        resId = R.string.drawer_connection_status_active
        colorId = R.color.power_on
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

  private fun setupToolbar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
  }

  private fun setupNavigationDrawer() {
    drawerToggle = ActionBarDrawerToggle(
      this,
      binding.drawerLayout,
      R.string.drawer_open,
      R.string.drawer_close
    )
    binding.drawerLayout.addDrawerListener(drawerToggle)
    val navHostFragment = supportFragmentManager.findFragmentById(
      R.id.main_navigation_fragment
    ) as NavHostFragment
    val navController = navHostFragment.navController
    setupWithNavController(binding.navView, navController)
    navController.addOnDestinationChangedListener(onNavigatedListener)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    drawerToggle.syncState()
    connectionStatusLiveDataProvider.observe(this) {
      onConnection(it)
    }

    if (connectionStatusLiveDataProvider.getValue() != ConnectionStatus.Active) {
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

  @OptIn(KoinExperimentalAPI::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    setupKoinFragmentFactory()
    super.onCreate(savedInstanceState)
    binding = ActivityNavigationBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupToolbar()
    setupNavigationDrawer()
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

  override fun onNavigateUp(): Boolean {
    return findNavController(R.id.main_navigation_fragment).navigateUp()
  }

  override fun onDestroy() {
    connectionStatusLiveDataProvider.removeObservers(this)
    val navController = findNavController(R.id.main_navigation_fragment)
    navController.removeOnDestinationChangedListener(onNavigatedListener)
    super.onDestroy()
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        volumeModifyUseCase.increment()
        true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        volumeModifyUseCase.decrement()
        true
      }
      else -> super.onKeyDown(keyCode, event)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // The action bar home/up action should open or close the drawer.
    // [ActionBarDrawerToggle] will take care of this.
    if (!drawerToggle.isDrawerIndicatorEnabled) {
      return findNavController(R.id.main_navigation_fragment).navigateUp()
    }

    if (drawerToggle.onOptionsItemSelected(item)) {
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    // Pass any configuration change to the drawer toggle.
    drawerToggle.onConfigurationChanged(newConfig)
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
