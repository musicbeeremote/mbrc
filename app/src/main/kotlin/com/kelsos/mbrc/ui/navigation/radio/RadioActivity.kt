package com.kelsos.mbrc.ui.navigation.radio;

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class RadioActivity : BaseActivity(), RadioView, OnRefreshListener, OnRadioPressedListener {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.radio_list) lateinit var radioView: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyViewTitle: TextView
  @BindView(R.id.list_empty_icon) lateinit var emptyViewIcon: ImageView
  @BindView(R.id.list_empty_subtitle) lateinit var emptyViewSubTitle: TextView
  @BindView(R.id.empty_view_progress_bar) lateinit var emptyViewProgress: ProgressBar

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
    ButterKnife.bind(this)
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
}
