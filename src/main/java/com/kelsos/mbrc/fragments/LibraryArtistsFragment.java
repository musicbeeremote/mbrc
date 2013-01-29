package com.kelsos.mbrc.fragments;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.squareup.otto.Bus;

public class LibraryArtistsFragment extends RoboSherlockListFragment {
    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    private Bus bus;


}
