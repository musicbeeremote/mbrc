package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;

public class ButtonFragment extends RoboSherlockFragment {
    @Inject private Bus bus;
    @InjectView(R.id.main_button_play_pause) private ImageButton playButton;
    @InjectView(R.id.main_button_previous) private ImageButton previousButton;
    @InjectView(R.id.main_button_next) private ImageButton nextButton;
    @InjectView(R.id.main_button_stop) private ImageButton stopButton;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.ui_main_buttons, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        playButton.setOnClickListener(playButtonListener);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
        stopButton.setOnClickListener(stopButtonListener);
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }
    private View.OnClickListener playButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerPlayPause, true));
        }
    };
    private View.OnClickListener previousButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerPrevious, true));
        }
    };
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerNext, true));
        }
    };
    private View.OnClickListener stopButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerStop, true));
        }
    };

    private void post(UserAction data) {
        bus.post(new MessageEvent(ProtocolEventType.UserAction, data));
    }
}
