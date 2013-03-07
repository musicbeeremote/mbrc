package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.views.MainFragmentActivity;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;


public class SlidingMenuFragment extends RoboSherlockFragment {

    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    Bus bus;

    @InjectView(R.id.track_rating_bar)
    RatingBar trackRatingBar;

    @InjectView(R.id.lfmLoveButton)
    ImageButton lfmLoveButton;

    @InjectView(R.id.lfmBanButton)
    ImageButton lfmBanButton;

    @InjectView(R.id.artistButton)
    ImageButton artistButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        afProvider.addActiveFragment(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        trackRatingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
        lfmBanButton.setOnClickListener(lfmBanClick);
        lfmLoveButton.setOnClickListener(lfmLoveClick);
        artistButton.setOnClickListener(artistButtonClick);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_menu, container, false);
    }

    @Override
    public void onDestroy()
    {
		super.onDestroy();
        afProvider.addActiveFragment(this);
    }

    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if(fromUser){
                bus.post(new MessageEvent(UserInputEvent.RequestRating, Float.toString(rating)));
            }
        }
    };

    private ImageButton.OnClickListener lfmLoveClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            bus.post(new MessageEvent(UserInputEvent.RequestLastFmLove));
        }
    };

    private ImageButton.OnClickListener lfmBanClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View view){
            bus.post(new MessageEvent(UserInputEvent.RequestLastFmBan));
        }
    };

    private ImageButton.OnClickListener artistButtonClick = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {

            SimpleLibrarySearchFragment slsFragment = new SimpleLibrarySearchFragment();
            replaceFragment(slsFragment);
        }
    };

    public void setRating(float rating){
        if(trackRatingBar!=null){
            trackRatingBar.setRating(rating);
        }
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(((MainFragmentActivity)getActivity()).isTablet() ? R.id.fragment_container_extra : R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        ((SlidingFragmentActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((SlidingFragmentActivity)getActivity()).showContent();
    }


}
