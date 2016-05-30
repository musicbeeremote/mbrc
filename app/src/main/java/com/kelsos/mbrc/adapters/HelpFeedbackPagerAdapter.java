package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.fragments.FeedbackFragment;
import com.kelsos.mbrc.ui.fragments.HelpFragment;

public class HelpFeedbackPagerAdapter extends FragmentStatePagerAdapter {
  private static final int HELP = 0;
  private static final int FEEDBACK = 1;
  private static final int PAGES = 2;
  private final Context context;

  private final int titles[] = new int[] {
      R.string.tab_help,
      R.string.tab_feedback
  };

  public HelpFeedbackPagerAdapter(FragmentManager fm, Context context) {
    super(fm);
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case HELP:
        return HelpFragment.newInstance();
      case FEEDBACK:
        return FeedbackFragment.newInstance();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return PAGES;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return context.getString(titles[position]);
  }
}
