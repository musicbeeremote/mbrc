package com.kelsos.mbrc.ui.navigation.radio;

import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class RadioActivity : BaseActivity(), RadioView {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  @BindView(R.id.swipe_layout) lateinit var swipeLayout: MultiSwipeRefreshLayout
  @BindView(R.id.radio_list) lateinit var playlistList: EmptyRecyclerView
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @BindView(R.id.list_empty_title) lateinit var emptyViewTitle: TextView

  @Inject lateinit var presenter: RadioPresenter

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
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    Toothpick.closeScope(this)
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}
