package com.kelsos.mbrc.fragments;

import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

public class SimpleLibrarySearchFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener {


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;  ||
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;  ||
    }
}
