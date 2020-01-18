package com.kelsos.mbrc.features.radio.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.snackbar
import com.kelsos.mbrc.common.utilities.nonNullObserver
import com.kelsos.mbrc.features.minicontrol.MiniControlFactory
import com.kelsos.mbrc.features.radio.presentation.RadioAdapter.OnRadioPressedListener
import kotterknife.bindView
import org.koin.android.ext.android.inject

class RadioFragment : Fragment(), OnRadioPressedListener {

  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.radio_stations__refresh_layout)
  private val radioView: RecyclerView by bindView(R.id.radio_stations__stations_list)
  private val emptyView: Group by bindView(R.id.radio_stations__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.radio_stations__text_title)
  private val emptyViewIcon: ImageView by bindView(R.id.radio_stations__empty_icon)
  private val emptyViewProgress: ProgressBar by bindView(R.id.radio_stations__loading_bar)

  private val viewModel: RadioViewModel by inject()
  private val adapter: RadioAdapter by inject()
  private val miniControlFactory: MiniControlFactory by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_radio, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.radios.nonNullObserver(this) { list ->
      emptyView.isVisible = list.isEmpty()
      emptyViewProgress.isVisible = false
      swipeLayout.isRefreshing = false
      adapter.submitList(list)
    }

    viewModel.emitter.nonNullObserver(this) { event ->
      event.contentIfNotHandled?.run {
        val resId = when (this) {
          RadioUiMessages.QueueFailed -> R.string.radio__queue_failed
          RadioUiMessages.QueueSuccess -> R.string.radio__queue_success
          RadioUiMessages.NetworkError -> R.string.radio__queue_network_error
          RadioUiMessages.RefreshSuccess -> R.string.radio__refresh_success
          RadioUiMessages.RefreshFailed -> R.string.radio__refresh_failed
        }
        snackbar(resId)
        swipeLayout.isRefreshing = false
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupEmptyView()
    setupRecycler()
    miniControlFactory.attach(requireFragmentManager())
  }

  private fun setupRecycler() {
    swipeLayout.setOnRefreshListener { viewModel.reload() }
    radioView.adapter = adapter
    radioView.layoutManager = LinearLayoutManager(requireContext())
    adapter.setOnRadioPressedListener(this)
  }

  private fun setupEmptyView() {
    emptyViewTitle.setText(R.string.radio__no_radio_stations)
    emptyViewIcon.setImageResource(R.drawable.ic_radio_black_80dp)
  }

  override fun onDestroy() {
    adapter.setOnRadioPressedListener(null)
    super.onDestroy()
  }

  override fun onRadioPressed(path: String) {
    viewModel.play(path)
  }
}