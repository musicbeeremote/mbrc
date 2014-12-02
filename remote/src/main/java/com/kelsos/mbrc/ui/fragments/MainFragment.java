package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;

import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.support.v7.widget.ShareActionProvider;
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
import com.kelsos.mbrc.events.ui.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class MainFragment extends RoboFragment {
    // Injects
    @Inject protected Bus bus;
    // Inject elements of the view
    @InjectView(R.id.main_artist_label) TextView artistLabel;
    @InjectView(R.id.main_title_label) TextView titleLabel;
    @InjectView(R.id.main_label_album) TextView albumLabel;
    @InjectView(R.id.main_label_year) TextView yearLabel;
    @InjectView(R.id.main_track_progress_current) TextView trackProgressCurrent;
    @InjectView(R.id.main_track_duration_total) TextView trackDuration;
    @InjectView(R.id.main_button_play_pause) ImageButton playPauseButton;
    @InjectView(R.id.main_button_previous) ImageButton previousButton;
    @InjectView(R.id.main_button_next) ImageButton nextButton;
    @InjectView(R.id.main_volume_seeker) SeekBar volumeSlider;
    @InjectView(R.id.main_track_progress_seeker) SeekBar trackProgressSlider;
    @InjectView(R.id.main_button_stop) ImageButton stopButton;
    @InjectView(R.id.main_mute_button) ImageButton muteButton;
    @InjectView(R.id.main_last_fm_button) ImageButton scrobbleButton;
    @InjectView(R.id.main_shuffle_button) ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button) ImageButton repeatButton;
    @InjectView(R.id.main_album_cover_image_view) ImageView albumCover;
    @InjectView(R.id.ratingWrapper) LinearLayout ratingWrapper;
    @InjectView(R.id.track_rating_bar) RatingBar trackRating;
    @InjectView(R.id.main_lfm_love_button) ImageButton lfmLoveButton;

    private ShareActionProvider mShareActionProvider;
    private boolean userChangingVolume;
    private int previousVol;
    private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mProgressUpdateHandler;

    private RatingBar.OnRatingBarChangeListener ratingChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (b) {
                bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingRating, v)));
            }
        }
    };
    private View.OnClickListener playButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerPlayPause, true)));
        }
    };
    private View.OnClickListener previousButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerPrevious, true)));
        }
    };
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerNext, true)));
        }
    };
    private View.OnClickListener stopButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerStop, true)));
        }
    };
    private View.OnClickListener muteButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerMute, Const.TOGGLE)));
        }
    };
    private View.OnClickListener scrobbleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerScrobble, Const.TOGGLE)));
        }
    };
    private View.OnClickListener shuffleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerShuffle, Const.TOGGLE)));
        }
    };
    private View.OnClickListener repeatButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerRepeat, Const.TOGGLE)));
        }
    };

    private View.OnLongClickListener lfmLongClickListener = new View.OnLongClickListener() {
        @Override public boolean onLongClick(View view) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingLfmRating, "Ban")));
            return true;
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerVolume, String.valueOf(seekBar.getProgress()))));
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
                    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingPosition, String.valueOf(progress))));
                    previousVol = progress;
                }

            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    private ImageView.OnClickListener coverOnClick = new ImageView.OnClickListener() {

        boolean isActive = false;

        @Override
        public void onClick(View view) {

            if (!isActive) {
                final int fadeInDuration = 300;
                final int timeBetween = 3000;
                final int fadeOutDuration = 800;

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(fadeInDuration);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(fadeInDuration + timeBetween);
                fadeOut.setDuration(fadeOutDuration);

                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                animation.addAnimation(fadeOut);
                animation.setRepeatCount(1);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {
                        isActive = true;
                    }

                    @Override public void onAnimationEnd(Animation animation) {
                        isActive = false;
                        ratingWrapper.setVisibility(View.INVISIBLE);
                    }

                    @Override public void onAnimationRepeat(Animation animation) {
                    }
                });
                ratingWrapper.setVisibility(View.VISIBLE);
                ratingWrapper.startAnimation(animation);
            }
        }
    };
    private ImageButton.OnClickListener lfmLoveClicked = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingLfmRating, Const.TOGGLE)));
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userChangingVolume = false;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_main, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        SetTextViewTypeface();
        RegisterListeners();
        bus.register(this);
    }

    @Override public void onResume() {
        super.onResume();
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        try {
            ratingWrapper.setVisibility(View.INVISIBLE);
            trackRating.setOnRatingBarChangeListener(ratingChangeListener);
            lfmLoveButton.setOnClickListener(lfmLoveClicked);
            lfmLoveButton.setOnLongClickListener(lfmLongClickListener);

            playPauseButton.setOnClickListener(playButtonListener);
            previousButton.setOnClickListener(previousButtonListener);
            nextButton.setOnClickListener(nextButtonListener);
            volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
            trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
            stopButton.setOnClickListener(stopButtonListener);
            stopButton.setEnabled(false);
            muteButton.setOnClickListener(muteButtonListener);
            scrobbleButton.setOnClickListener(scrobbleButtonListener);
            shuffleButton.setOnClickListener(shuffleButtonListener);
            repeatButton.setOnClickListener(repeatButtonListener);
            albumCover.setOnClickListener(coverOnClick);
        } catch (Exception ignore) {

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
            yearLabel.setSelected(true);

            Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
            Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
            artistLabel.setTypeface(robotoLight);
            titleLabel.setTypeface(robotoLight);
            albumLabel.setTypeface(robotoLight);
            yearLabel.setTypeface(robotoLight);
            trackProgressCurrent.setTypeface(robotoRegular);
            trackDuration.setTypeface(robotoRegular);
        } catch (Exception ignore) {

        }
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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

    @Subscribe public void handleRatingChange(RatingChanged event) {
        if (trackRating != null) {
            trackRating.setRating(event.getRating());
        }
    }

    @Subscribe public void handleScrobbleChange(ScrobbleChange change) {
        if (scrobbleButton == null) return;
        scrobbleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_scrobble_red : R.drawable.ic_media_scrobble_off);
    }

    @Subscribe public void handleCoverEvent(final CoverAvailable cevent) {
        if (albumCover == null) return;
        if (cevent.getIsAvailable()) {
            albumCover.setImageBitmap(cevent.getCover());
        } else {
            albumCover.setImageResource(R.drawable.ic_image_no_cover);
        }
    }

    @Subscribe public void handleShuffleChange(ShuffleChange change) {
        if (shuffleButton == null) return;
        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    @Subscribe public void updateRepeatButtonState(RepeatChange change) {
        if (repeatButton == null) return;
        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    @Subscribe public void updateVolumeData(VolumeChange change) {
        if (volumeSlider == null) return;
        if (!userChangingVolume)
            volumeSlider.setProgress(change.getVolume());
        if (muteButton == null) return;
        muteButton.setImageResource(change.getIsMute() ? R.drawable.ic_media_mute_active : R.drawable.ic_media_mute_full);
    }

    @Subscribe public void handlePlayStateChange(final PlayStateChange change) {
        if (playPauseButton == null || stopButton == null) return;
        switch (change.getState()) {
            case Playing:
                playPauseButton.setImageResource(R.drawable.ic_media_pause);
                playPauseButton.setTag("Playing");
                stopButton.setImageResource(R.drawable.ic_media_stop);
                stopButton.setEnabled(true);				/* Start the animation if the track is playing*/
                bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
                trackProgressAnimation();
                break;
            case Paused:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                playPauseButton.setTag("Paused");
                stopButton.setEnabled(true);
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                activateStoppedState();
            case Undefined:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                stopButton.setImageResource(R.drawable.ic_media_stop_disabled);
                stopButton.setEnabled(false);
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
        if (!stopButton.isEnabled() || playPauseButton.getTag() == "Paused") return;

        final Runnable updateProgress = new Runnable() {
            @Override public void run() {

                int currentProgress = trackProgressSlider.getProgress() / 1000;
                final int currentMinutes = currentProgress / 60;
                final int currentSeconds = currentProgress % 60;

                if (getActivity() == null) return;

                getActivity().runOnUiThread(new Runnable() {

                    @Override public void run() {
                        try {
                            if (trackProgressSlider == null) return;
                            trackProgressSlider.setProgress(trackProgressSlider.getProgress() + 1000);
                            trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                        } catch (Exception ex) {
                            if (BuildConfig.DEBUG) {
                                Log.d("mbrc-log:","animation timer", ex);
                            }
                        }
                    }
                });
            }
        };

        mProgressUpdateHandler = progressScheduler.scheduleAtFixedRate(updateProgress, 0,
                TIME_PERIOD, TimeUnit.SECONDS);

    }

    private void activateStoppedState() {
        if (trackProgressCurrent == null || trackProgressSlider == null || stopButton == null) return;
        trackProgressSlider.setProgress(0);
        trackProgressCurrent.setText("00:00");
        stopButton.setEnabled(false);
    }

    @Subscribe public void handleTrackInfoChange(final TrackInfoChange change) {
        if (artistLabel == null) return;
        artistLabel.setText(change.getArtist());
        titleLabel.setText(change.getTitle());
        albumLabel.setText(change.getAlbum());
        yearLabel.setText(change.getYear());

        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(getShareIntent());
    }

    @Subscribe public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        if (change.getStatus() == ConnectionStatus.CONNECTION_OFF) {
            stopTrackProgressAnimation();
            activateStoppedState();
        }
    }

    @Subscribe public void handleLfmStatusChange(final LfmRatingChanged event) {
        switch (event.getStatus()) {
            case LOVED:
                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_loved);
                break;
            case BANNED:
                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_banned);
                break;
            case NORMAL:
                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_unloved);
                break;
        }
    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     */
    @Subscribe public void handlePositionUpdate(UpdatePosition position) {
        final int total = position.getTotal();
        final int current = position.getCurrent();
        if (trackProgressCurrent == null || trackProgressSlider == null || trackDuration == null) return;
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
