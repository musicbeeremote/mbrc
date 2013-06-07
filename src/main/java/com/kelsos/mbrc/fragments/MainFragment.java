package com.kelsos.mbrc.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.others.Const;
import com.kelsos.mbrc.others.Protocol;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends RoboSherlockFragment {
    // Injects
    @Inject protected Bus bus;
    // Inject elements of the view
    @InjectView(R.id.main_artist_label)
    TextView artistLabel;
    @InjectView(R.id.main_title_label)
    TextView titleLabel;
    @InjectView(R.id.main_label_album)
    TextView albumLabel;
    @InjectView(R.id.main_label_year)
    TextView yearLabel;
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
    @InjectView(R.id.main_button_stop)
    ImageButton stopButton;
    @InjectView(R.id.main_mute_button)
    ImageButton muteButton;
    @InjectView(R.id.main_last_fm_button)
    ImageButton scrobbleButton;
    @InjectView(R.id.main_shuffle_button)
    ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button)
    ImageButton repeatButton;
    @InjectView(R.id.main_button_connect)
    ImageButton connectivityIndicator;
    @InjectView(R.id.main_album_cover_image_view)
    ImageView albumCover;
    @InjectView(R.id.ratingWrapper)
    LinearLayout ratingWrapper;
    @InjectView(R.id.track_rating_bar)
    RatingBar trackRating;
    @InjectView(R.id.loveWrapper)
    LinearLayout loveWrapper;
    @InjectView(R.id.lfmLove)
    ImageButton lfmLoveButton;
    @Inject ActiveFragmentProvider afProvide;
    private ShareActionProvider mShareActionProvider;
    private boolean userChangingVolume;
    private int previousVol;
    private Timer progressUpdateTimer;
    private TimerTask progressUpdateTask;
    private RatingBar.OnRatingBarChangeListener ratingChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingRating, v)));
        }
    };
    private View.OnClickListener playButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerPlayPause, true)));
        }
    };
    private View.OnClickListener previousButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerPrevious, true)));
        }
    };
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerNext, true)));
        }
    };
    private View.OnClickListener stopButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerStop, true)));
        }
    };
    private View.OnClickListener muteButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerMute, true)));
        }
    };
    private View.OnClickListener scrobbleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerScrobble, true)));
        }
    };
    private View.OnClickListener shuffleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerShuffle, Const.TOGGLE)));
        }
    };
    private View.OnClickListener repeatButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerRepeat, true)));
        }
    };
    private View.OnClickListener connectivityIndicatorListener = new View.OnClickListener() {

        public void onClick(View v) {
            bus.post(new MessageEvent(UserInputEvent.StartConnection));
        }
    };
    private View.OnLongClickListener connectivityIndicatorLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            bus.post(new MessageEvent(UserInputEvent.ResetConnection));
            return false;
        }
    };
    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerVolume, String.valueOf(seekBar.getProgress()))));
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
                    bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingPosition, String.valueOf(progress))));
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
                int fadeInDuration = 600;
                int timeBetween = 3000;
                int fadeOutDuration = 1000;

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

                ratingWrapper.startAnimation(animation);
                loveWrapper.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isActive = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isActive = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        ||
                    }
                });
            }
        }
    };
    private ImageButton.OnClickListener lfmLoveClicked = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingLfmRating, Const.TOGGLE)));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        afProvide.addActiveFragment(this);
        userChangingVolume = false;
        bus.register(this);
        bus.post(new MessageEvent(UserInputEvent.RequestMainViewUpdate));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SetTextViewTypeface();
        afProvide.addActiveFragment(this);
        RegisterListeners();
        bus.post(new MessageEvent(UserInputEvent.RequestMainViewUpdate));

    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        try {
            ratingWrapper.setVisibility(View.INVISIBLE);
            loveWrapper.setVisibility(View.INVISIBLE);
            trackRating.setOnRatingBarChangeListener(ratingChangeListener);
            lfmLoveButton.setOnClickListener(lfmLoveClicked);

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
            connectivityIndicator.setOnClickListener(connectivityIndicatorListener);
            connectivityIndicator.setOnLongClickListener(connectivityIndicatorLongClickListener);
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

    @Override
    public void onResume() {
        super.onResume();
        afProvide.addActiveFragment(this);
        bus.post(new MessageEvent(UserInputEvent.RequestMainViewUpdate));
        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
    }

    @Override
    public void onPause() {
        afProvide.removeActiveFragment(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        afProvide.removeActiveFragment(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        afProvide.removeActiveFragment(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem shareItem = menu.findItem(R.id.main_menu_share);

        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Now Playing: " + artistLabel.getText() + " - " + titleLabel.getText());
                setShareIntent(shareIntent);
                return true;
            default:
                return false;
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) mShareActionProvider.setShareIntent(shareIntent);
    }

    public void updateRating(float rating) {
        if (trackRating != null) {
            trackRating.setRating(rating);
        }
    }

    /**
     * Given a boolean state this function updates the Scrobbler button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state If true it means that the scrobbler is active, false is used for inactive.
     */
    public void updateScrobblerButtonState(boolean state) {
        if (scrobbleButton == null) return;
        if (state) {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_red);
        } else {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_off);
        }
    }

    @Subscribe
    public void handleCoverEvent(final CoverAvailable cevent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (albumCover == null) return;
                if (cevent.getIsAvailable()) {
                    albumCover.setImageBitmap(cevent.getCover());
                } else {
                    albumCover.setImageResource(R.drawable.ic_image_no_cover);
                }
            }
        });
    }

    /**
     * Given a boolean state value this function updates the shuffle button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state True is used to represent active shuffle, false is used for inactive.
     */
    public void updateShuffleButtonState(boolean state) {
        if (shuffleButton == null) return;
        if (state) {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
        } else {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle_off);
        }
    }

    public void updateRepeatButtonState(boolean state) {
        if (repeatButton == null) return;
        if (state) {
            repeatButton.setImageResource(R.drawable.ic_media_repeat);
        } else {
            repeatButton.setImageResource(R.drawable.ic_media_repeat_off);
        }
    }

    @Subscribe
    public void updateVolumeData(VolumeChange change) {
        if (volumeSlider == null) return;
        if (!userChangingVolume)
            volumeSlider.setProgress(change.getVolume());
        if (muteButton == null) return;
        muteButton.setImageResource(change.getIsMute() ? R.drawable.ic_media_mute_active : R.drawable.ic_media_mute_full);
    }

    @Subscribe
    public void handlePlayStateChange(final PlayStateChange change) {
        if (playPauseButton == null || stopButton == null) return;
        switch (change.getState()) {
            case Playing:
                playPauseButton.setImageResource(R.drawable.ic_media_pause);
                playPauseButton.setTag("Playing");
                stopButton.setImageResource(R.drawable.ic_media_stop);
                stopButton.setEnabled(true);				/* Start the animation if the track is playing*/
                bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
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
        if (progressUpdateTask != null) {
            progressUpdateTask.cancel();
            progressUpdateTask = null;
            progressUpdateTimer.cancel();
            progressUpdateTimer.purge();
            progressUpdateTimer = null;
        }

    }

    /**
     * Starts the progress animation when called. If It was previously running then it restarts it.
     */
    private void trackProgressAnimation() {
        if (!isVisible()) return;
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        final int timerPeriod = 100;
        stopTrackProgressAnimation();
        if (!stopButton.isEnabled() || playPauseButton.getTag() == "Paused") return;
        progressUpdateTimer = new Timer(true);
        progressUpdateTask = new TimerTask() {
            @Override
            public void run() {
                int currentProgress = trackProgressSlider.getProgress() / 1000;
                final int currentMinutes = currentProgress / 60;
                final int currentSeconds = currentProgress % 60;
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (trackProgressSlider == null) return;
                            trackProgressSlider.setProgress(trackProgressSlider.getProgress() + timerPeriod);
                            trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                        } catch (Exception ex) {
                            Log.d("MBRC", "TimerEx", ex);
                        }
                    }
                });

            }
        };
        progressUpdateTimer.schedule(progressUpdateTask, 0, timerPeriod);
    }

    private void activateStoppedState() {
        if (trackProgressCurrent == null || trackProgressSlider == null || stopButton == null) return;
        trackProgressSlider.setProgress(0);
        trackProgressCurrent.setText("00:00");
        stopButton.setEnabled(false);
    }

    @Subscribe
    public void handleTrackInfoChange(final TrackInfoChange change) {

        if (artistLabel == null) return;
        artistLabel.setText(change.getArtist());
        titleLabel.setText(change.getTitle());
        albumLabel.setText(change.getAlbum());
        yearLabel.setText(change.getYear());

    }

    @Subscribe
    public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectivityIndicator == null) return;
                switch (change.getStatus()) {
                    case CONNECTION_OFF:
                        connectivityIndicator.setImageResource(R.drawable.ic_connectivy_off);
                        stopTrackProgressAnimation();
                        activateStoppedState();
                        break;
                    case CONNECTION_ON:
                        connectivityIndicator.setImageResource(R.drawable.ic_connectivity_connected);
                        break;
                    case CONNECTION_ACTIVE:
                        connectivityIndicator.setImageResource(R.drawable.ic_connectivity_active);
                        break;
                }
            }
        });

    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     */
    @Subscribe
    public void handlePositionUpdate(UpdatePosition position) {
        final int total = position.getTotal();
        final int current = position.getCurrent();
        if (trackProgressCurrent == null || trackProgressSlider == null || trackDuration == null) return;
        if (total == 0) {
            bus.post(new MessageEvent(UserInputEvent.RequestPosition));
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                trackDuration.setText(String.format("%02d:%02d", totalMinutes, finalTotalSeconds));
                trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, finalCurrentSeconds));

                trackProgressSlider.setMax(total);
                trackProgressSlider.setProgress(current);
            }
        });


        trackProgressAnimation();
    }
}
