package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.model.PlayerState;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.actions.ButtonPressedEvent;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.Logger;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.actions.ButtonPressedEvent.Button;

public class ButtonFragment extends RoboFragment {

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

    @Inject
    private RemoteApi api;

    @Inject
    private PlayerState playerStateModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_buttons, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        playButton.setOnClickListener(v ->
                Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.PLAYPAUSE)));

        playButton.setOnLongClickListener(v -> {
            Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.STOP));
            return true;
        });

        previousButton.setOnClickListener(v ->
                Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.PREVIOUS)));

        nextButton.setOnClickListener(v ->
                Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.NEXT)));

        shuffleButton.setOnClickListener(v ->
                Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.SHUFFLE)));

        repeatButton.setOnClickListener(v ->
                Events.ButtonPressedNotification.onNext(new ButtonPressedEvent(Button.REPEAT)));

        Subscription sub = AndroidObservable.bindFragment(this, playerStateModel.playState())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::UpdatePlaystate, Logger::LogThrowable);
    }


    public void handleShuffleChange(ShuffleChange change) {
        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    public void updateRepeatButtonState(RepeatChange change) {
        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    public void UpdatePlaystate(final PlayState state) {
        int resId = R.drawable.ic_media_play;
        switch (state) {
            case PLAYING:
                resId = R.drawable.ic_media_pause;
                break;
            case STOPPED:
                resId = R.drawable.ic_media_stop;
                break;
            default:
                break;
        }
        playButton.setImageResource(resId);
    }

}
