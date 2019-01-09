package com.kelsos.mbrc.ui.helpfeedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentHelpFeedbackBinding

class HelpFeedbackFragment : Fragment() {

  private lateinit var pagerAdapter: HelpFeedbackPagerAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = FragmentHelpFeedbackBinding.inflate(inflater, container, false)
    pagerAdapter = HelpFeedbackPagerAdapter(requireActivity())
    binding.pagerHelpFeedback.apply {
      adapter = pagerAdapter
    }
    TabLayoutMediator(
      binding.feedbackTabLayout,
      binding.pagerHelpFeedback
    ) { currentTab, currentPosition ->
      currentTab.text = when (currentPosition) {
        HelpFeedbackPagerAdapter.HELP -> getString(R.string.tab_help)
        HelpFeedbackPagerAdapter.FEEDBACK -> getString(R.string.tab_feedback)
        else -> throw IllegalArgumentException("invalid position")
      }
    }.attach()
    return binding.root
  }
}
