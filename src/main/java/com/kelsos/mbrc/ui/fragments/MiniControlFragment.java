package com.kelsos.mbrc.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.model.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;



public class MiniControlFragment extends RoboSherlockFragment {

    @Inject Bus bus;
    @InjectView (R.id.mc_track_cover) ImageView trackCover;
    @InjectView (R.id.mc_track_artist) TextView trackArtist;
    @InjectView (R.id.mc_track_title) TextView trackTitle;
    @InjectView (R.id.mc_next_track) ImageButton playNext;
    @InjectView (R.id.mc_play_pause) ImageButton playPause;
    @InjectView (R.id.mc_prev_track) ImageButton playPrevious;

    private Typeface robotoLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        playNext.setOnClickListener(playNextListener);
        playPause.setOnClickListener(playPauseListener);
        playPrevious.setOnClickListener(playPreviousListener);
        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
        trackTitle.setTypeface(robotoLight);
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe public void handleCoverChange(CoverAvailable event) {
        if (trackCover == null) return;
        if (event.getIsAvailable()) {
            trackCover.setImageBitmap(event.getCover());
        } else {
            trackCover.setImageResource(R.drawable.ic_image_no_cover);
        }
    }

    @Subscribe public void handleTrackInfoChange(TrackInfoChange event) {
        trackArtist.setText(event.getArtist());
        trackTitle.setText(event.getTitle());
    }

    ImageButton.OnClickListener playNextListener = new ImageButton.OnClickListener(){

        @Override public void onClick(View view) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerNext, true)));
        }
    };

    ImageButton.OnClickListener playPauseListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View view) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerPlayPause, true)));
        }
    };

    ImageButton.OnClickListener playPreviousListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View view) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerPrevious, true)));
        }
    };

    @Subscribe public void handlePlayStateChange(PlayStateChange event) {
        switch (event.getState()) {
            case Playing:
                playPause.setImageResource(R.drawable.ic_action_pause);
                break;
            case Paused:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case Stopped:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case Undefined:
                break;
        }
    }

}
