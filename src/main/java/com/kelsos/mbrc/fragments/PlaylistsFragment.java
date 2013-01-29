package com.kelsos.mbrc.fragments;

import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.squareup.otto.Bus;

public class PlaylistsFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener {

    @Inject
    ActiveFragmentProvider afp;

    @Inject
    Bus bus;

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
