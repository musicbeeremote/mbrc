package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;

public class ButtonFragment extends BaseFragment {
    @InjectView(R.id.main_button_play_pause) private ImageButton playButton;
    @InjectView(R.id.main_button_previous) private ImageButton previousButton;
    @InjectView(R.id.main_button_next) private ImageButton nextButton;
    @InjectView(R.id.main_shuffle_button) private ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button) private ImageButton repeatButton;

    @Inject
    private RemoteApi api;
    private View.OnClickListener playButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            api.playbackStart(new Callback<SuccessResponse>() {
                @Override
                public void success(SuccessResponse successResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    };
    private View.OnClickListener previousButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            api.playPrevious(new Callback<SuccessResponse>() {
                @Override
                public void success(SuccessResponse successResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    };
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            api.playNext(new Callback<SuccessResponse>() {
                @Override
                public void success(SuccessResponse successResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    };
    private View.OnLongClickListener stopListener = new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
            api.playbackStop(new Callback<SuccessResponse>() {
                @Override
                public void success(SuccessResponse successResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
            return true;
        }
    };
    private ImageButton.OnClickListener shuffleListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View v) {
            //api.updateShuffleState();
        }
    };
    private ImageButton.OnClickListener repeatListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View v) {
            //api.updateRepeatState()
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
