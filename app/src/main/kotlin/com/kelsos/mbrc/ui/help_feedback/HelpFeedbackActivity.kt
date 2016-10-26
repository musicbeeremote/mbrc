package com.kelsos.mbrc.ui.help_feedback

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.HelpFeedbackPagerAdapter
import com.kelsos.mbrc.ui.activities.FontActivity

class HelpFeedbackActivity : FontActivity() {

  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.feedback_tab_layout) lateinit var tabLayout: TabLayout
  @BindView(R.id.pager_help_feedback) lateinit var viewPager: ViewPager

  private var pagerAdapter: HelpFeedbackPagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_help_feedback)
    ButterKnife.bind(this)
    setSupportActionBar(toolbar)
    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeButtonEnabled(true)
    }

    pagerAdapter = HelpFeedbackPagerAdapter(supportFragmentManager, this)
    viewPager.adapter = pagerAdapter
    tabLayout.setupWithViewPager(viewPager)
  }
}
