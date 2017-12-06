package com.kelsos.mbrc.ui.navigation.radio

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class RadioActivity : BaseNavigationActivity(), RadioView, OnRefreshListener, OnRadioPressedListener {

  private val swipeLayout: MultiSwipeRefreshLayout by bindView(R.id.swipe_layout)
  private val radioView: EmptyRecyclerView by bindView(R.id.radio_list)
  private val emptyView: View by bindView(R.id.empty_view)
  private val emptyViewTitle: TextView by bindView(R.id.list_empty_title)
  private val emptyViewIcon: ImageView by bindView(R.id.list_empty_icon)
  private val emptyViewSubTitle: TextView by bindView(R.id.list_empty_subtitle)
  private val emptyViewProgress: ProgressBar by bindView(R.id.empty_view_progress_bar)

  @Inject lateinit var presenter: RadioPresenter
  @Inject lateinit var adapter: RadioAdapter

  override fun active(): Int {
    return R.id.nav_radio
  }

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
    swipeLayout.setSwipeableChildren(R.id.radio_list, R.id.empty_view)
    emptyViewTitle.setText(R.string.radio__no_radio_stations)
    emptyViewIcon.setImageResource(R.drawable.ic_radio_black_80dp)
    radioView.adapter = adapter
    radioView.emptyView = emptyView
    radioView.layoutManager = LinearLayoutManager(this)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
    presenter.load()
    adapter.setOnRadioPressedListener(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
    adapter.setOnRadioPressedListener(null)
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    Toothpick.closeScope(this)
  }

  override fun update(data: FlowCursorList<RadioStation>) {
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
    emptyViewProgress.visibility = View.VISIBLE
    emptyViewIcon.visibility = View.GONE
    emptyViewTitle.visibility = View.GONE
    emptyViewSubTitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyViewProgress.visibility = View.GONE
    emptyViewIcon.visibility = View.VISIBLE
    emptyViewTitle.visibility = View.VISIBLE
    emptyViewSubTitle.visibility = View.VISIBLE
    swipeLayout.isRefreshing = false
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
