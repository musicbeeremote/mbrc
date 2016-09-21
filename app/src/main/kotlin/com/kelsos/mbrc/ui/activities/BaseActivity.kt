package com.kelsos.mbrc.ui.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.controller.RemoteService
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.events.ui.RequestConnectionStateEvent
import com.kelsos.mbrc.ui.activities.nav.*
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

abstract class BaseActivity : FontActivity(), NavigationView.OnNavigationItemSelectedListener {
  @Inject lateinit var bus: RxBus
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.drawer_layout) lateinit var drawer: DrawerLayout
  @BindView(R.id.nav_view) lateinit var navigationView: NavigationView

  private var connectText: TextView? = null
  private var toggle: ActionBarDrawerToggle? = null
  private var connect: ImageView? = null
  private var scope: Scope? = null

  private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.name == service.service.className) {
        return true
      }
    }
    return false
  }

  protected abstract fun active(): Int

  private fun onConnectLongClick(view: View): Boolean {
    ifNotRunningStartService()
    bus.post(MessageEvent(UserInputEventType.ResetConnection))
    return true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope!!.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  private fun onConnectClick(view: View) {
    ifNotRunningStartService()
    bus.post(MessageEvent(UserInputEventType.StartConnection))
  }

  private fun ifNotRunningStartService() {
    if (!isMyServiceRunning(RemoteService::class.java)) {
      startService(Intent(this, RemoteService::class.java))
    }
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
    drawer.removeDrawerListener(toggle!!)
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
    toggle!!.syncState()
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> return true
      KeyEvent.KEYCODE_VOLUME_DOWN -> return true
      else -> return super.onKeyUp(keyCode, event)
    }
  }

  private fun onConnection(event: ConnectionStatusChangeEvent) {
    Timber.v("Handling new connection status %s", event.status)
    @StringRes val resId: Int
    @ColorRes val colorId: Int
    if (event.status == Connection.OFF) {
      resId = R.string.drawer_connection_status_off
      colorId = R.color.black
    } else if (event.status == Connection.ON) {
      resId = R.string.drawer_connection_status_on
      colorId = R.color.accent
    } else if (event.status == Connection.ACTIVE) {
      resId = R.string.drawer_connection_status_active
      colorId = R.color.power_on
    } else {
      resId = R.string.drawer_connection_status_off
      colorId = R.color.black
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
    when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        bus.post(MessageEvent(UserInputEventType.KeyVolumeUp))
        return true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        bus.post(MessageEvent(UserInputEventType.KeyVolumeDown))
        return true
      }
      else -> return super.onKeyDown(keyCode, event)
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    drawer.closeDrawer(GravityCompat.START)
    drawer.postDelayed({ navigate(itemId) }, 250)
    return true
  }

  private fun navigate(itemId: Int) {

    if (active() == itemId) {
      return
    }

    if (itemId == R.id.nav_home) {
      createBackStack(Intent(this, MainActivity::class.java))
    } else if (itemId == R.id.nav_library) {
      createBackStack(Intent(this, LibraryActivity::class.java))
    } else if (itemId == R.id.nav_now_playing) {
      createBackStack(Intent(this, NowPlayingActivity::class.java))
    } else if (itemId == R.id.nav_playlists) {
      createBackStack(Intent(this, PlaylistActivity::class.java))
    } else if (itemId == R.id.nav_lyrics) {
      createBackStack(Intent(this, LyricsActivity::class.java))
    } else if (itemId == R.id.nav_settings) {
      createBackStack(Intent(this, SettingsActivity::class.java))
    } else if (itemId == R.id.nav_help) {
      createBackStack(Intent(this, HelpFeedbackActivity::class.java))
    } else if (itemId == R.id.nav_exit) {
      stopService(Intent(this, RemoteService::class.java))
      finish()
    }
  }

  private fun createBackStack(intent: Intent) {
    val builder = TaskStackBuilder.create(this)
    builder.addNextIntentWithParentStack(intent)
    builder.startActivities()
    overridePendingTransition(0, 0)
  }

  /**
   * Should be called after RoboGuice injections and Butterknife bindings.
   */
  fun setup() {
    Timber.v("Initializing base activity")
    ifNotRunningStartService()
    setSupportActionBar(toolbar)

    toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    drawer.addDrawerListener(toggle!!)
    drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
    toggle!!.syncState()
    navigationView.setNavigationItemSelectedListener(this)

    val header = navigationView.getHeaderView(0)
    connectText = ButterKnife.findById<TextView>(header, R.id.nav_connect_text)
    connect = ButterKnife.findById<ImageView>(header, R.id.connect_button)
    connect!!.setOnClickListener({ this.onConnectClick(it) })
    connect!!.setOnLongClickListener({ this.onConnectLongClick(it) })

    val actionBar = supportActionBar
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeButtonEnabled(true)
    }

    navigationView.setCheckedItem(active())
  }

  override fun onResume() {
    super.onResume()
    this.bus.register(this, NotifyUser::class.java, { this.handleUserNotification(it) }, true)
    this.bus.register(this, ConnectionStatusChangeEvent::class.java, { this.onConnection(it) }, true)
    this.bus.post(RequestConnectionStateEvent())
  }

  override fun onPause() {
    super.onPause()
    this.bus.unregister(this)
  }
}

