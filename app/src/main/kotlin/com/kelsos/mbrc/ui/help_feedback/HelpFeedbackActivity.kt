package com.kelsos.mbrc.ui.help_feedback

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ActivityHelpFeedbackBinding
import com.kelsos.mbrc.ui.activities.BaseActivity

class HelpFeedbackActivity : BaseActivity() {

  private lateinit var pagerAdapter: HelpFeedbackPagerAdapter
  private lateinit var binding: ActivityHelpFeedbackBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityHelpFeedbackBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupToolbar()

    pagerAdapter = HelpFeedbackPagerAdapter(this)
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
  }
}
