package com.kelsos.mbrc.ui.helpfeedback

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R

class HelpFeedbackPagerAdapter(
  fm: androidx.fragment.app.FragmentManager,
  context: Activity
) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
  private val context: Context

  private val titles = intArrayOf(R.string.tab_help, R.string.tab_feedback)

  init {
    this.context = context
  }

  override fun getItem(position: Int): androidx.fragment.app.Fragment? {
    return when (position) {
      HELP -> HelpFragment.newInstance()
      FEEDBACK -> FeedbackFragment.newInstance()
      else -> null
    }
  }

  override fun getCount(): Int {
    return PAGES
  }

  override fun getPageTitle(position: Int): CharSequence {
    return context.getString(titles[position])
  }

  companion object {
    private const val HELP = 0
    private const val FEEDBACK = 1
    private const val PAGES = 2
  }
}