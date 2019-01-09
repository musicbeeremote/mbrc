package com.kelsos.mbrc.ui.navigation.radio

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
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.extensions.snackbar
import com.kelsos.mbrc.ui.navigation.radio.RadioAdapter.OnRadioPressedListener
import kotterknife.bindView
import org.koin.android.ext.android.inject

class RadioFragment : Fragment(), OnRefreshListener, OnRadioPressedListener {

  private val swipeLayout: SwipeRefreshLayout by bindView(R.id.radio_stations__refresh_layout)
  private val radioView: RecyclerView by bindView(R.id.radio_stations__stations_list)
  private val emptyView: Group by bindView(R.id.radio_stations__empty_group)
  private val emptyViewTitle: TextView by bindView(R.id.radio_stations__text_title)
  private val emptyViewIcon: ImageView by bindView(R.id.radio_stations__empty_icon)
  private val emptyViewProgress: ProgressBar by bindView(R.id.radio_stations__loading_bar)

  private val presenter: RadioViewModel by inject()
  private val adapter: RadioAdapter by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_radio, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupEmptyView()
    setupRecycler()
  }

  private fun setupRecycler() {
    swipeLayout.setOnRefreshListener(this)
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

  fun update(data: PagedList<RadioStationEntity>) {
    emptyView.isVisible = data.isEmpty()
    adapter.submitList(data)
  }

  fun error(error: Throwable) {
    snackbar(R.string.radio__loading_failed)
  }

  override fun onRadioPressed(path: String) {
    presenter.play(path)
  }

  override fun onRefresh() {
    presenter.refresh()
  }

  fun radioPlayFailed(error: Throwable?) {
    snackbar(R.string.radio__play_failed)
  }

  fun radioPlaySuccessful() {
    snackbar(R.string.radio__play_successful)
  }

  fun loading(visible: Boolean) {
    if (!visible) {
      emptyViewProgress.isVisible = false
      swipeLayout.isRefreshing = false
    }
  }
}