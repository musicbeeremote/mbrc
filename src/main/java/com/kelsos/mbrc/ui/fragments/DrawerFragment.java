package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.activities.AppPreferenceView;
import com.kelsos.mbrc.ui.activities.MainFragmentActivity;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;


public class DrawerFragment extends RoboSherlockFragment {
    @Inject Bus bus;
    @InjectView(R.id.menuLibrary) TextView menuLibrary;
    @InjectView(R.id.menuLyrics) TextView menuLyrics;
    @InjectView(R.id.menuNowPlaying) TextView menuNowPlaying;
    @InjectView(R.id.menuSettings) TextView menuSettings;
    private Typeface robotoLight;

    private TextView.OnClickListener libraryButtonClick = new TextView.OnClickListener() {

        @Override
        public void onClick(View view) {

            SearchFragment slsFragment = new SearchFragment();
            replaceFragment(slsFragment);
            closeDrawer();
        }
    };
    private TextView.OnClickListener settingsButtonClick = new TextView.OnClickListener() {

        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), AppPreferenceView.class));
        }
    };
    private TextView.OnClickListener nowPlayingButtonClick = new TextView.OnClickListener() {

        @Override
        public void onClick(View view) {
            NowPlayingFragment npFragment = new NowPlayingFragment();
            replaceFragment(npFragment);
            closeDrawer();
        }
    };
    private TextView.OnClickListener lyricsButtonClick = new TextView.OnClickListener() {

        @Override
        public void onClick(View view) {
            LyricsFragment lFragment = new LyricsFragment();
            replaceFragment(lFragment);
            closeDrawer();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_drawer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        menuLibrary.setOnClickListener(libraryButtonClick);
        menuSettings.setOnClickListener(settingsButtonClick);
        menuNowPlaying.setOnClickListener(nowPlayingButtonClick);
        menuLyrics.setOnClickListener(lyricsButtonClick);

        menuLibrary.setTypeface(robotoLight);
        menuSettings.setTypeface(robotoLight);
        menuNowPlaying.setTypeface(robotoLight);
        menuLyrics.setTypeface(robotoLight);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(((MainFragmentActivity) getActivity()).isTablet() ? R.id.fragment_container_extra : R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        ((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void closeDrawer() {
        MainFragmentActivity activity = ((MainFragmentActivity) getActivity());
        if (activity != null) {
            activity.closeDrawer();
        }
    }


}
