package com.kelsos.mbrc.ui.navigation.radio

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.databinding.ActivityRadioBinding
import com.kelsos.mbrc.databinding.ListEmptyViewBinding
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class RadioActivity :
  BaseNavigationActivity(),
  RadioView,
  SwipeRefreshLayout.OnRefreshListener,
  OnRadioPressedListener {

  @Inject lateinit var presenter: RadioPresenter
  @Inject lateinit var adapter: RadioAdapter

  override fun active(): Int {
    return R.id.nav_radio
  }

  private lateinit var scope: Scope
  private lateinit var binding: ActivityRadioBinding
  private lateinit var emptyBinding: ListEmptyViewBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(PRESENTER_SCOPE).installModules(RadioModule())
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    binding = ActivityRadioBinding.inflate(layoutInflater)
    emptyBinding = ListEmptyViewBinding.bind(binding.root)
    setContentView(binding.root)
    Toothpick.inject(this, scope)

    super.setup()
    binding.swipeLayout.setOnRefreshListener(this)
    binding.swipeLayout.setSwipeableChildren(R.id.radio_list, R.id.empty_view)
    emptyBinding.listEmptyTitle.setText(R.string.radio__no_radio_stations)
    emptyBinding.listEmptyIcon.setImageResource(R.drawable.ic_radio_black_80dp)
    binding.radioList.adapter = adapter
    binding.radioList.emptyView = emptyBinding.emptyView
    binding.radioList.layoutManager = LinearLayoutManager(this)
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

  override fun update(data: List<RadioStation>) {
    adapter.update(data)
  }

  override fun error(error: Throwable) {
    Snackbar.make(binding.radioList, R.string.radio__loading_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun onRadioPressed(path: String) {
    presenter.play(path)
  }

  override fun onRefresh() {
    presenter.refresh()
  }

  override fun radioPlayFailed() {
    Snackbar.make(binding.radioList, R.string.radio__play_failed, Snackbar.LENGTH_SHORT).show()
  }

  override fun radioPlaySuccessful() {
    Snackbar.make(binding.radioList, R.string.radio__play_successful, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoading() {
    emptyBinding.emptyViewProgressBar.visibility = View.VISIBLE
    emptyBinding.listEmptyIcon.visibility = View.GONE
    emptyBinding.listEmptyTitle.visibility = View.GONE
    emptyBinding.listEmptySubtitle.visibility = View.GONE
  }

  override fun hideLoading() {
    emptyBinding.emptyViewProgressBar.visibility = View.GONE
    emptyBinding.listEmptyIcon.visibility = View.VISIBLE
    emptyBinding.listEmptyTitle.visibility = View.VISIBLE
    emptyBinding.listEmptySubtitle.visibility = View.VISIBLE
    binding.swipeLayout.isRefreshing = false
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
