package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.actions.*;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

public class ButtonFragment extends BaseFragment {

    @InjectView(R.id.main_button_play_pause)
    private ImageButton playButton;

    @InjectView(R.id.main_button_previous)
    private ImageButton previousButton;

    @InjectView(R.id.main_button_next)
    private ImageButton nextButton;

    @InjectView(R.id.main_shuffle_button)
    private ImageButton shuffleButton;

    @InjectView(R.id.main_repeat_button)
    private ImageButton repeatButton;

    private View.OnClickListener playButtonListener = v -> getBus().post(new PlayPressedEvent());
    private View.OnClickListener previousButtonListener = v -> getBus().post(new PreviousPressedEvent());
    private View.OnClickListener nextButtonListener = v -> getBus().post(new NextPressedEvent());
    private View.OnLongClickListener stopListener = v -> {
        getBus().post(new StopPressedEvent());
        return true;
    };
    private ImageButton.OnClickListener shuffleListener = v -> getBus().post(new ShufflePressedEvent());
    private ImageButton.OnClickListener repeatListener = v -> getBus().post(new RepeatChangeEvent());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_buttons, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        playButton.setOnClickListener(playButtonListener);
        playButton.setOnLongClickListener(stopListener);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
        shuffleButton.setOnClickListener(shuffleListener);
        repeatButton.setOnClickListener(repeatListener);
    }

    @Subscribe
    public void handleShuffleChange(ShuffleChange change) {
        if (shuffleButton == null) {
            return;
        }
        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    @Subscribe
    public void updateRepeatButtonState(RepeatChange change) {
        if (repeatButton == null) {
            return;
        }
        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    @Subscribe
    public void handlePlayStateChange(final PlayStateChange change) {
        if (playButton == null) {
            return;
        }
        switch (change.getState()) {
            case PLAYING:
                playButton.setImageResource(R.drawable.ic_media_pause);
                break;
            case PAUSED:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            case STOPPED:
                playButton.setImageResource(R.drawable.ic_media_stop);
                break;
            case UNDEFINED:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            default:
                break;
        }
    }

}
