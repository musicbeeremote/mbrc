package com.kelsos.mbrc.ui.helpfeedback

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.BaseActivity
import kotterknife.bindView

class HelpFeedbackActivity : BaseActivity() {

  private val tabLayout: TabLayout by bindView(R.id.feedback_tab_layout)
  private val viewPager: ViewPager by bindView(R.id.pager_help_feedback)

  private var pagerAdapter: HelpFeedbackPagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_help_feedback)
    setupToolbar()

    pagerAdapter = HelpFeedbackPagerAdapter(supportFragmentManager, this)
    viewPager.adapter = pagerAdapter
    tabLayout.setupWithViewPager(viewPager)
  }
}