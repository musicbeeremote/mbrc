package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

public class ButtonFragment extends BaseFragment {
    @InjectView(R.id.main_button_play_pause) private ImageButton playButton;
    @InjectView(R.id.main_button_previous) private ImageButton previousButton;
    @InjectView(R.id.main_button_next) private ImageButton nextButton;
    @InjectView(R.id.main_shuffle_button) private ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button) private ImageButton repeatButton;
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
    private View.OnLongClickListener stopListener = new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
            post(new UserAction(Protocol.PlayerStop, true));
            return true;
        }
    };
    private ImageButton.OnClickListener shuffleListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View v) {
            post(new UserAction(Protocol.PlayerShuffle, Const.TOGGLE));
        }
    };
    private ImageButton.OnClickListener repeatListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View v) {
            post(new UserAction(Protocol.PlayerRepeat, Const.TOGGLE));
        }
    };

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_buttons, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        playButton.setOnClickListener(playButtonListener);
        playButton.setOnLongClickListener(stopListener);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
        shuffleButton.setOnClickListener(shuffleListener);
        repeatButton.setOnClickListener(repeatListener);
    }

    @Subscribe public void handleShuffleChange(ShuffleChange change) {
        if (shuffleButton == null) {
            return;
        }
        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    @Subscribe public void updateRepeatButtonState(RepeatChange change) {
        if (repeatButton == null) {
            return;
        }
        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    @Subscribe public void handlePlayStateChange(final PlayStateChange change) {
        if (playButton == null) {
            return;
        }
        switch (change.getState()) {
            case Playing:
                playButton.setImageResource(R.drawable.ic_media_pause);
                post(new UserAction(Protocol.NowPlayingPosition, true));
                break;
            case Paused:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            case Stopped:
                playButton.setImageResource(R.drawable.ic_media_stop);
                break;
            case Undefined:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            default:
                break;
        }
    }

    private void post(UserAction data) {
        getBus().post(new MessageEvent(ProtocolEventType.UserAction, data));
    }
}
