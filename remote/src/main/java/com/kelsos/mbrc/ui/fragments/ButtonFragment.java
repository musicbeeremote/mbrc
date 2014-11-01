package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.actions.RepeatChangeEvent;
import com.kelsos.mbrc.events.actions.ShufflePressedEvent;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.rest.RemoteApi;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private View.OnClickListener playButtonListener = v ->
            api.playbackStart()
                    .flatMap(resp -> api.getPlaystate())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> UpdatePlaystate(PlayState.valueOf(resp.getValue().toUpperCase())));


    private View.OnLongClickListener stopListener = v -> {
        return true;
    };
    private ImageButton.OnClickListener shuffleListener = v -> new ShufflePressedEvent();
    private ImageButton.OnClickListener repeatListener = v -> new RepeatChangeEvent();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_buttons, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        playButton.setOnClickListener(playButtonListener);
        playButton.setOnLongClickListener(stopListener);

        previousButton.setOnClickListener(v ->
                api.playPrevious()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> Ln.d(resp.isSuccess())));

        nextButton.setOnClickListener(v ->
                api.playNext()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> Ln.d(resp.isSuccess())));

        shuffleButton.setOnClickListener(shuffleListener);
        repeatButton.setOnClickListener(repeatListener);
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
