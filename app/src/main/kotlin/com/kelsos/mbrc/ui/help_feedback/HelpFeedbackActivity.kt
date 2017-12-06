package com.kelsos.mbrc.ui.help_feedback

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.FontActivity
import kotterknife.bindView

class HelpFeedbackActivity : FontActivity() {

  private val toolbar: Toolbar by bindView(R.id.toolbar)
  private val tabLayout: TabLayout by bindView(R.id.feedback_tab_layout)
  private val viewPager: ViewPager by bindView(R.id.pager_help_feedback)

  private var pagerAdapter: HelpFeedbackPagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_help_feedback)

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
