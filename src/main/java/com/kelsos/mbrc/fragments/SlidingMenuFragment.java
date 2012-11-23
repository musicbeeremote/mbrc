package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.UserActionEvent;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;


public class SlidingMenuFragment extends RoboSherlockFragment {

    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    Bus bus;

    @InjectView(R.id.track_rating_bar)
    RatingBar trackRatingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        afProvider.addActiveFragment(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        trackRatingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_menu, container, false);
    }

    @Override
    public void onDestroy()
    {
        afProvider.addActiveFragment(this);
    }

    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if(fromUser){
                bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_RATING_CHANGE, Float.toString(rating)));
            }
        }
    };

    public void setRating(float rating){
        if(trackRatingBar!=null){
            trackRatingBar.setRating(rating);
        }
    }
}
