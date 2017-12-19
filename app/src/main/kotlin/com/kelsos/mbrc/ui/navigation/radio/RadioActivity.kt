package com.kelsos.mbrc.ui.navigation.radio

import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.extensions.gone
import com.kelsos.mbrc.extensions.hide
import com.kelsos.mbrc.extensions.show
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class RadioActivity : BaseNavigationActivity(), RadioView, OnRefreshListener, OnRadioPressedListener {

  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.radio_stations__refresh_layout)
  private val radioView: RecyclerView by bindView(R.id.radio_stations__stations_list)
  private val emptyView: Group by bindView(R.id.radio_stations__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.radio_stations__text_title)
  private val emptyViewIcon: ImageView by bindView(R.id.radio_stations__empty_icon)
  private val emptyViewProgress: ProgressBar by bindView(R.id.radio_stations__loading_bar)

  @Inject lateinit var presenter: RadioPresenter
  @Inject lateinit var adapter: RadioAdapter

  override fun active(): Int = R.id.nav_radio

  private lateinit var scope: Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(PRESENTER_SCOPE).installModules(RadioModule())
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_radio)
    Toothpick.inject(this, scope)

    super.setup()
    swipeLayout.setOnRefreshListener(this)
    emptyViewTitle.setText(R.string.radio__no_radio_stations)
    emptyViewIcon.setImageResource(R.drawable.ic_radio_black_80dp)
    radioView.adapter = adapter
    radioView.layoutManager = LinearLayoutManager(this)
    presenter.attach(this)
    presenter.load()
    adapter.setOnRadioPressedListener(this)
  }

  override fun onDestroy() {
    presenter.detach()
    adapter.setOnRadioPressedListener(null)

    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun update(data: List<RadioStation>) {
    if (data.isEmpty()) {
      emptyView.show()
    } else {
      emptyView.hide()
    }
    adapter.update(data)
  }

  override fun error(error: Throwable) {
    Snackbar.make(radioView, R.string.radio__loading_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onRadioPressed(path: String) {
    presenter.play(path)
  }

  override fun onRefresh() {
    presenter.refresh()
  }

  override fun radioPlayFailed(error: Throwable?) {
    Snackbar.make(radioView, R.string.radio__play_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun radioPlaySuccessful() {
    Snackbar.make(radioView, R.string.radio__play_successful, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {

  }

  override fun hideLoading() {
    emptyViewProgress.gone()
    swipeLayout.isRefreshing = false
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
