package com.kelsos.mbrc.ui.navigation.radio

import android.os.Bundle
import androidx.core.view.isGone
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.databinding.ActivityRadioBinding
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

  override fun active(): Int = R.id.nav_radio

  private lateinit var scope: Scope
  private lateinit var binding: ActivityRadioBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    Toothpick.openScope(PRESENTER_SCOPE).installModules(RadioModule())
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    binding = ActivityRadioBinding.inflate(layoutInflater)
    setContentView(binding.root)
    Toothpick.inject(this, scope)
    super.setup()
    binding.radioStationsRefreshLayout.setOnRefreshListener(this)
    binding.radioStationsStationsList.adapter = adapter
    binding.radioStationsStationsList.layoutManager = LinearLayoutManager(this)
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

  override suspend fun update(data: PagingData<RadioStation>) {
    adapter.submitData(data)
    binding.radioStationsEmptyGroup.isGone = adapter.itemCount != 0
  }

  override fun error(error: Throwable) {
    showSnackbar(R.string.radio__loading_failed)
  }

  override fun onRadioPressed(path: String) {
    presenter.play(path)
  }

  override fun onRefresh() {
    presenter.refresh()
  }

  override fun radioPlayFailed() {
    showSnackbar(R.string.radio__play_failed)
  }

  override fun radioPlaySuccessful() {
    showSnackbar(R.string.radio__play_successful)
  }

  override fun loading(visible: Boolean) {
    if (!visible) {
      binding.radioStationsLoadingBar.isGone = true
      binding.radioStationsRefreshLayout.isRefreshing = false
    }
  }

  @javax.inject.Scope
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  }
}
