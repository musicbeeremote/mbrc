package com.kelsos.mbrc.ui.helpfeedback

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R

class HelpFeedbackPagerAdapter(
  fm: FragmentManager,
  context: Activity
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  private val context: Context
  private val titles = intArrayOf(R.string.tab_help, R.string.tab_feedback)

  init {
    this.context = context
  }

  override fun getItem(position: Int): Fragment {
    return when (position) {
      HELP -> HelpFragment.newInstance()
      FEEDBACK -> FeedbackFragment.newInstance()
      else -> error("Invalid position $position")
    }
  }

  override fun getCount(): Int = PAGES

  override fun getPageTitle(position: Int): CharSequence {
    return context.getString(titles[position])
  }

  companion object {
    private const val HELP = 0
    private const val FEEDBACK = 1
    private const val PAGES = 2
  }
}