package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.events.ui.VolumeChange;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@Singleton
public class MainFragment extends RoboFragment {
    // Injects
    @Inject
    protected Bus bus;
    // Inject elements of the view
    @InjectView(R.id.main_artist_label)
    TextView artistLabel;
    @InjectView(R.id.main_title_label)
    TextView titleLabel;
    @InjectView(R.id.main_label_album)
    TextView albumLabel;
    @InjectView(R.id.main_track_progress_current)
    TextView trackProgressCurrent;
    @InjectView(R.id.main_track_duration_total)
    TextView trackDuration;
    @InjectView(R.id.main_button_play_pause)
    ImageButton playPauseButton;
    @InjectView(R.id.main_button_previous)
    ImageButton previousButton;
    @InjectView(R.id.main_button_next)
    ImageButton nextButton;
    @InjectView(R.id.main_volume_seeker)
    SeekBar volumeSlider;
    @InjectView(R.id.main_track_progress_seeker)
    SeekBar trackProgressSlider;
    @InjectView(R.id.main_mute_button)
    ImageButton muteButton;
    @InjectView(R.id.main_shuffle_button)
    ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button)
    ImageButton repeatButton;
    @InjectView(R.id.main_album_cover_image_view)
    ImageView albumCover;

    private ShareActionProvider mShareActionProvider;
    private boolean userChangingVolume;
    private int previousVol;
    private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mProgressUpdateHandler;

    private View.OnClickListener playButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerPlayPause, true);
        postAction(action);
    };

    private View.OnClickListener previousButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerPrevious, true);
        postAction(action);
    };

    private View.OnClickListener nextButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerNext, true);
        postAction(action);
    };

    private View.OnLongClickListener stopButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerStop, true);
        postAction(action);
        return true;
    };

    private View.OnClickListener muteButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerMute, Const.TOGGLE);
        postAction(action);
    };

    private View.OnClickListener shuffleButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerShuffle, Const.TOGGLE);
        postAction(action);
    };

    private View.OnClickListener repeatButtonListener = v -> {
        final UserAction action = new UserAction(Protocol.PlayerRepeat, Const.TOGGLE);
        postAction(action);
    };

    /**
     * Posts a user action wrapped in a MessageEvent. The bus will
     * pass the MessageEvent through the Socket to the plugin.
     *
     * @param action Any kind of UserAction available in the Protocol
     */
    private void postAction(UserAction action) {
        bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
    }

    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                final UserAction action = new UserAction(Protocol.PlayerVolume, String.valueOf(seekBar.getProgress()));
                postAction(action);
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            userChangingVolume = false;

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            userChangingVolume = true;
        }


    };
    private SeekBar.OnSeekBarChangeListener durationSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress != previousVol) {
                    final UserAction action = new UserAction(Protocol.NowPlayingPosition, String.valueOf(progress));
                    postAction(action);
                    previousVol = progress;
                }

            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userChangingVolume = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SetTextViewTypeface();
        RegisterListeners();
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        final UserAction action = new UserAction(Protocol.NowPlayingPosition, true);
        bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        try {
            playPauseButton.setOnClickListener(playButtonListener);
            previousButton.setOnClickListener(previousButtonListener);
            nextButton.setOnClickListener(nextButtonListener);
            volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
            trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
            playPauseButton.setOnLongClickListener(stopButtonListener);
            muteButton.setOnClickListener(muteButtonListener);
            shuffleButton.setOnClickListener(shuffleButtonListener);
            repeatButton.setOnClickListener(repeatButtonListener);
        } catch (Exception ignore) {
            Ln.d(ignore);
        }
    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void SetTextViewTypeface() {		/* Marquee Hack */
        try {
            artistLabel.setSelected(true);
            titleLabel.setSelected(true);
            albumLabel.setSelected(true);

            Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
            Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_medium.ttf");
            artistLabel.setTypeface(robotoRegular);
            titleLabel.setTypeface(robotoMedium);
            albumLabel.setTypeface(robotoMedium);
            trackProgressCurrent.setTypeface(robotoMedium);
            trackDuration.setTypeface(robotoMedium);
        } catch (Exception ignore) {
            Ln.d(ignore);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
        MenuItem shareItem = menu.findItem(R.id.actionbar_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getShareIntent());
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        final String payload = String.format("Now Playing: %s - %s", artistLabel.getText(), titleLabel.getText());
        shareIntent.putExtra(Intent.EXTRA_TEXT, payload);
        return shareIntent;
    }

    @Subscribe
    public void handleCoverEvent(final CoverAvailable cevent) {
        if (albumCover == null) return;
        if (cevent.getIsAvailable()) {
            albumCover.setImageBitmap(cevent.getCover());
        } else {
            albumCover.setImageResource(R.drawable.ic_image_no_cover);
        }
    }

    @Subscribe
    public void handleShuffleChange(ShuffleChange change) {
        if (shuffleButton == null) return;

        int color = getResources().getColor(!change.getShuffleState().equals(ShuffleChange.OFF)
                ? R.color.colorAccent
                : R.color.button_material_dark);
        shuffleButton.setColorFilter(color);

        shuffleButton.setImageResource(change.getShuffleState().equals(ShuffleChange.AUTODJ)
                ? R.drawable.ic_headset_grey600_24dp
                : R.drawable.ic_shuffle_grey600_24dp);
    }

    @Subscribe
    public void updateRepeatButtonState(RepeatChange change) {
        if (repeatButton == null) return;
        int color = getResources().getColor(change.getIsActive() ? R.color.colorAccent : R.color.button_material_dark);
        repeatButton.setColorFilter(color);
    }

    @Subscribe
    public void updateVolumeData(VolumeChange change) {
        if (volumeSlider == null) return;
        if (!userChangingVolume)
            volumeSlider.setProgress(change.getVolume());
        if (muteButton == null) return;
        muteButton.setImageResource(change.getIsMute() ? R.drawable.ic_volume_off_grey600_24dp : R.drawable.ic_volume_up_grey600_24dp);
    }

    @Subscribe
    public void handlePlayStateChange(final PlayStateChange change) {
        if (playPauseButton == null) return;
        switch (change.getState()) {
            case Playing:
                playPauseButton.setImageResource(R.drawable.ic_pause_circle_fill);
                playPauseButton.setTag("Playing");

                /* Start the animation if the track is playing*/
                bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
                trackProgressAnimation();
                break;
            case Paused:
                playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
                playPauseButton.setTag("Paused");
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
                playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
                playPauseButton.setTag("Stopped");
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                activateStoppedState();
                break;
            case Undefined:
                playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
                break;
        }
    }

    /**
     * If the track progress animation is running the the function stops it.
     */
    private void stopTrackProgressAnimation() {
        if (mProgressUpdateHandler != null) {
            mProgressUpdateHandler.cancel(true);
        }
    }

    /**
     * Starts the progress animation when called. If It was previously running then it restarts it.
     */
    private void trackProgressAnimation() {
        if (!isVisible()) return;
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        final int TIME_PERIOD = 1;
        stopTrackProgressAnimation();
        if (playPauseButton.getTag().equals("Stopped") || playPauseButton.getTag().equals("Paused"))
            return;

        final Runnable updateProgress = () -> {

            int currentProgress = trackProgressSlider.getProgress() / 1000;
            final int currentMinutes = currentProgress / 60;
            final int currentSeconds = currentProgress % 60;

            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {
                try {
                    if (trackProgressSlider == null) return;
                    trackProgressSlider.setProgress(trackProgressSlider.getProgress() + 1000);
                    trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                } catch (Exception ex) {
                    if (BuildConfig.DEBUG) {
                        Log.d("mbrc-log:", "animation timer", ex);
                    }
                }
            });
        };

        mProgressUpdateHandler = progressScheduler.scheduleAtFixedRate(updateProgress, 0,
                TIME_PERIOD, TimeUnit.SECONDS);

    }

    private void activateStoppedState() {
        if (trackProgressCurrent == null || trackProgressSlider == null) return;
        trackProgressSlider.setProgress(0);
        trackProgressCurrent.setText("00:00");
    }

    @Subscribe
    public void handleTrackInfoChange(final TrackInfoChange change) {
        if (artistLabel == null) return;
        artistLabel.setText(change.getArtist());
        titleLabel.setText(change.getTitle());
        albumLabel.setText(TextUtils.isEmpty(change.getYear())
                ? change.getAlbum()
                : String.format("%s [%s]", change.getAlbum(), change.getYear()));

        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(getShareIntent());
    }

    @Subscribe
    public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        if (change.getStatus() == ConnectionStatus.CONNECTION_OFF) {
            stopTrackProgressAnimation();
            activateStoppedState();
        }
    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     */
    @Subscribe
    public void handlePositionUpdate(UpdatePosition position) {
        final int total = position.getTotal();
        final int current = position.getCurrent();
        if (trackProgressCurrent == null || trackProgressSlider == null || trackDuration == null)
            return;
        if (total == 0) {
            bus.post(new MessageEvent(UserInputEventType.RequestPosition));
            return;
        }
        int currentSeconds = current / 1000;
        int totalSeconds = total / 1000;

        final int currentMinutes = currentSeconds / 60;
        final int totalMinutes = totalSeconds / 60;

        currentSeconds %= 60;
        totalSeconds %= 60;
        final int finalTotalSeconds = totalSeconds;
        final int finalCurrentSeconds = currentSeconds;

        trackDuration.setText(String.format("%02d:%02d", totalMinutes, finalTotalSeconds));
        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, finalCurrentSeconds));

        trackProgressSlider.setMax(total);
        trackProgressSlider.setProgress(current);

        trackProgressAnimation();
    }
}
