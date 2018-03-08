package com.kelsos.mbrc.ui.helpfeedback

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R

class HelpFeedbackPagerAdapter(fm: FragmentManager, context: Activity) : FragmentStatePagerAdapter(fm) {
  private val context: Context

  private val titles = intArrayOf(R.string.tab_help, R.string.tab_feedback)

  init {
    this.context = context
  }

  override fun getItem(position: Int): Fragment? {
    when (position) {
      HELP -> return HelpFragment.newInstance()
      FEEDBACK -> return FeedbackFragment.newInstance()
      else -> return null
    }
  }

  override fun getCount(): Int {
    return PAGES
  }

  override fun getPageTitle(position: Int): CharSequence {
    return context.getString(titles[position])
  }

  companion object {
    private val HELP = 0
    private val FEEDBACK = 1
    private val PAGES = 2
  }
}