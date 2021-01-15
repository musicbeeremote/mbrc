package com.kelsos.mbrc.ui.helpfeedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.FragmentHelpFeedbackBinding

class HelpFeedbackFragment : Fragment() {
  private val pagerAdapter: HelpFeedbackPagerAdapter by lazy {
    val activity = requireActivity()
    HelpFeedbackPagerAdapter(activity.supportFragmentManager, requireActivity())
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding: FragmentHelpFeedbackBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_help_feedback,
      container,
      false
    )
    binding.pagerHelpFeedback.adapter = pagerAdapter
    binding.feedbackTabLayout.setupWithViewPager(binding.pagerHelpFeedback)
    return binding.root
  }
}
