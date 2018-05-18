package com.kelsos.mbrc.ui.activities

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.platform.RemoteService
import com.kelsos.mbrc.platform.ServiceChecker
import com.kelsos.mbrc.ui.dialogs.OutputSelectionDialog
import com.kelsos.mbrc.ui.helpfeedback.HelpFeedbackActivity
import com.kelsos.mbrc.ui.navigation.library.LibraryActivity
import com.kelsos.mbrc.ui.navigation.lyrics.LyricsActivity
import com.kelsos.mbrc.ui.navigation.main.MainActivity
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingActivity
import com.kelsos.mbrc.ui.navigation.playlists.PlaylistActivity
import com.kelsos.mbrc.ui.navigation.radio.RadioActivity
import com.kelsos.mbrc.ui.preferences.SettingsActivity
import kotterknife.bindView
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseNavigationActivity : BaseActivity(),
  NavigationView.OnNavigationItemSelectedListener {

  @Inject
  lateinit var serviceChecker: ServiceChecker

  @Inject
  lateinit var volumeInteractor: VolumeInteractor

  @Inject
  lateinit var connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider

  private val toolbar: Toolbar by bindView(R.id.toolbar)
  private val drawer: DrawerLayout by bindView(R.id.drawer_layout)
  private val navigationView: NavigationView by bindView(R.id.nav_view)

  private var connectText: TextView? = null
  private var toggle: ActionBarDrawerToggle? = null
  private var connect: ImageView? = null

  protected abstract fun active(): Int

  private fun onConnectLongClick(): Boolean {
    serviceChecker.startServiceIfNotRunning()
    //bus.post(ChangeConnectionStateEvent(SocketAction.RESET))
    return true
  }

  private fun onConnectClick() {
    serviceChecker.startServiceIfNotRunning()
    //bus.post(ChangeConnectionStateEvent(SocketAction.START))
  }

  override fun onDestroy() {
    super.onDestroy()
    drawer.removeDrawerListener(toggle!!)
    connectionStatusLiveDataProvider.get().removeObservers(this)
  }

  override fun onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    toggle!!.onConfigurationChanged(newConfig)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    connectionStatusLiveDataProvider.get().observe(this, Observer {
      if (it == null) {
        return@Observer
      }
      onConnection(it)
    })
    toggle!!.syncState()
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> true
      KeyEvent.KEYCODE_VOLUME_DOWN -> true
      else -> super.onKeyUp(keyCode, event)
    }
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

    connectText!!.setText(resId)
    connect!!.setColorFilter(ContextCompat.getColor(this, colorId))
  }

  private fun handleUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message

    val focus = currentFocus
    if (focus != null) {
      Snackbar.make(focus, message, Snackbar.LENGTH_SHORT).show()
    }
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

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    drawer.run {
      closeDrawer(GravityCompat.START)
      postDelayed({ navigate(itemId) }, 250)
    }
    return true
  }

  private fun startActivity(clazz: KClass<*>) {
    createBackStack(Intent(this, clazz.java))
  }

  protected fun navigate(itemId: Int) {

    if (active() == itemId) {
      return
    }

    when (itemId) {
      R.id.nav_home -> {
        val upIntent = NavUtils.getParentActivityIntent(this) ?: error("couldn't get intent")
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
          startActivity(MainActivity::class)
        } else {
          NavUtils.navigateUpTo(this, upIntent)
        }
      }
      R.id.nav_library -> startActivity(LibraryActivity::class)
      R.id.nav_now_playing -> startActivity(NowPlayingActivity::class)
      R.id.nav_playlists -> startActivity(PlaylistActivity::class)
      R.id.nav_radio -> startActivity(RadioActivity::class)
      R.id.nav_lyrics -> startActivity(LyricsActivity::class)
      R.id.nav_settings -> startActivity(SettingsActivity::class)
      R.id.nav_outputs -> {
        val selectionDialog = OutputSelectionDialog.instance(supportFragmentManager)
        selectionDialog.show()
      }
      R.id.nav_help -> startActivity(HelpFeedbackActivity::class)
      R.id.nav_exit -> exitApplication()
    }
  }

  internal fun exitApplication() {
    stopService(Intent(this, RemoteService::class.java))

    if (this is MainActivity) {
      finish()
    } else {
      val intent = Intent(this, MainActivity::class.java)
      intent.putExtra(EXIT_APP, true)
      intent.flags = FLAG_ACTIVITY_CLEAR_TOP
      startActivity(intent)
    }
  }

  private fun createBackStack(intent: Intent) {
    TaskStackBuilder.create(this)
      .addNextIntentWithParentStack(intent)
      .startActivities()
    //overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
  }

  /**
   * Should be called after injections and
   */
  fun setup() {
    Timber.v("Initializing base activity")
    setSupportActionBar(toolbar)

    toggle = ActionBarDrawerToggle(
      this,
      drawer,
      toolbar,
      R.string.drawer_open,
      R.string.drawer_close
    ).apply {
      drawer.addDrawerListener(this)
      drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
      syncState()
    }

    navigationView.setNavigationItemSelectedListener(this)

    val header = navigationView.getHeaderView(0)
    connectText = header.findViewById(R.id.nav_connect_text)
    connect = header.findViewById<ImageView>(R.id.connect_button).apply {
      setOnClickListener({ onConnectClick() })
      setOnLongClickListener({ onConnectLongClick() })
    }

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }

    navigationView.setCheckedItem(active())
    serviceChecker.startServiceIfNotRunning()
  }

  companion object {
    const val EXIT_APP = "mbrc.exit"
  }
}