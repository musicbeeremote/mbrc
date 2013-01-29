package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.fragments.SlidingMenuFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdateRatingCommand implements ICommand {
    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    MainDataModel model;

    @Override
    public void execute(IEvent e) {
        if (afProvider.getActiveFragment(SlidingMenuFragment.class) != null)
        {
            Activity cActivity = afProvider.getActiveFragment(SlidingMenuFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ((SlidingMenuFragment) afProvider.getActiveFragment(SlidingMenuFragment.class)).setRating(model.getRating());
                }
            });
        }
    }
}
