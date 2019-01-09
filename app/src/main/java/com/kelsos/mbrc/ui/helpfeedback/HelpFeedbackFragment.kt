package com.kelsos.mbrc.ui.helpfeedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kelsos.mbrc.R
import kotterknife.bindView

class HelpFeedbackFragment : Fragment() {

  private val tabLayout: TabLayout by bindView(R.id.feedback_tab_layout)
  private val viewPager: ViewPager by bindView(R.id.pager_help_feedback)

  private val pagerAdapter: HelpFeedbackPagerAdapter by lazy {
    val activity = requireActivity()
    HelpFeedbackPagerAdapter(activity.supportFragmentManager, requireActivity())
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_help_feedback, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewPager.adapter = pagerAdapter
    tabLayout.setupWithViewPager(viewPager)
  }
}