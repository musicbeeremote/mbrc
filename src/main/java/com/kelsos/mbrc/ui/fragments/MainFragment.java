package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.InfoButtonPagerAdapter;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Singleton
public class MainFragment extends BaseFragment {
    // Injects
    @Inject protected Bus bus;
    // Inject elements of the view
    @InjectView(R.id.main_track_progress_current) TextView trackProgressCurrent;
    @InjectView(R.id.main_track_duration_total) TextView trackDuration;
    @InjectView(R.id.main_volume_seeker) SeekBar volumeSlider;
    @InjectView(R.id.main_track_progress_seeker) SeekBar trackProgressSlider;
    @InjectView(R.id.main_album_cover_image_view) ImageView albumCover;
    @InjectView(R.id.ratingWrapper) LinearLayout ratingWrapper;
    @InjectView(R.id.track_rating_bar) RatingBar trackRating;

    private ShareActionProvider mShareActionProvider;
    private boolean userChangingVolume;
    private int previousVol;
    private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mProgressUpdateHandler;
    private InfoButtonPagerAdapter mAdapter;

    private RatingBar.OnRatingBarChangeListener ratingChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (b) {
                post(new UserAction(Protocol.NowPlayingRating, v));
            }
        }
    };

    private void post(UserAction data) {
        bus.post(new MessageEvent(ProtocolEventType.UserAction, data));
    }

    private View.OnClickListener muteButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerMute, Const.TOGGLE));
        }
    };
    private View.OnClickListener scrobbleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerScrobble, Const.TOGGLE));
        }
    };
    private View.OnClickListener shuffleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerShuffle, Const.TOGGLE));
        }
    };
    private View.OnClickListener repeatButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            post(new UserAction(Protocol.PlayerRepeat, Const.TOGGLE));
        }
    };

    private View.OnLongClickListener lfmLongClickListener = new View.OnLongClickListener() {
        @Override public boolean onLongClick(View view) {
            post(new UserAction(Protocol.NowPlayingLfmRating, "Ban"));
            return true;
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                post(new UserAction(Protocol.PlayerVolume, String.valueOf(seekBar.getProgress())));
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
                    post(new UserAction(Protocol.NowPlayingPosition, String.valueOf(progress)));
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
            post(new UserAction(Protocol.NowPlayingLfmRating, Const.TOGGLE));
        }
    };
    private ViewPager mPager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userChangingVolume = false;
        mAdapter = new InfoButtonPagerAdapter(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_main, container, false);
        mPager = (ViewPager) view.findViewById(R.id.mbrc_main_infopager);
        mPager.setAdapter(mAdapter);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        SetTextViewTypeface();
        RegisterListeners();
        bus.register(this);
    }

    @Override public void onResume() {
        super.onResume();
        post(new UserAction(Protocol.NowPlayingPosition, true));
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        try {
            ratingWrapper.setVisibility(View.INVISIBLE);
            trackRating.setOnRatingBarChangeListener(ratingChangeListener);
            volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
            trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
            albumCover.setOnClickListener(coverOnClick);
        } catch (Exception ignore) {

        }

    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void SetTextViewTypeface() {		/* Marquee Hack */
        try {

            Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
            Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
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
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                //shareIntent.putExtra(Intent.EXTRA_TEXT, "Now Playing: " + artistLabel.getText() + " - " + titleLabel.getText());
                setShareIntent(shareIntent);
                return true;
            default:
                return false;
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) mShareActionProvider.setShareIntent(shareIntent);
    }

    @Subscribe public void handleRatingChange(RatingChanged event) {
        if (trackRating != null) {
            trackRating.setRating(event.getRating());
        }
    }

    @Subscribe public void handleScrobbleChange(ScrobbleChange change) {
//        if (scrobbleButton == null) return;
//        scrobbleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_scrobble_red : R.drawable.ic_media_scrobble_off);
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
//        if (shuffleButton == null) return;
//        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    @Subscribe public void updateRepeatButtonState(RepeatChange change) {
//        if (repeatButton == null) return;
//        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    @Subscribe public void updateVolumeData(VolumeChange change) {
        if (volumeSlider == null) return;
        if (!userChangingVolume)
            volumeSlider.setProgress(change.getVolume());
//        if (muteButton == null) return;
//        muteButton.setImageResource(change.getIsMute() ? R.drawable.ic_media_mute_active : R.drawable.ic_media_mute_full);
    }

//    @Subscribe public void handlePlayStateChange(final PlayStateChange change) {
//        if (playPauseButton == null || stopButton == null) return;
//        switch (change.getState()) {
//            case Playing:
//                playPauseButton.setImageResource(R.drawable.ic_media_pause);
//                playPauseButton.setTag("Playing");
//                stopButton.setImageResource(R.drawable.ic_media_stop);
//                stopButton.setEnabled(true);				/* Start the animation if the track is playing*/
//                post(new UserAction(Protocol.NowPlayingPosition, true));
//                trackProgressAnimation();
//                break;
//            case Paused:
//                playPauseButton.setImageResource(R.drawable.ic_media_play);
//                playPauseButton.setTag("Paused");
//                stopButton.setEnabled(true);
//        /* Stop the animation if the track is paused*/
//                stopTrackProgressAnimation();
//                break;
//            case Stopped:
//        /* Stop the animation if the track is paused*/
//                stopTrackProgressAnimation();
//                activateStoppedState();
//            case Undefined:
//                playPauseButton.setImageResource(R.drawable.ic_media_play);
//                stopButton.setImageResource(R.drawable.ic_media_stop_disabled);
//                stopButton.setEnabled(false);
//                break;
//        }


//    }

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
//        if (!isVisible()) return;
//        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
//        final int TIME_PERIOD = 1;
//        stopTrackProgressAnimation();
//        if (!stopButton.isEnabled() || playPauseButton.getTag() == "Paused") return;
//
//        final Runnable updateProgress = new Runnable() {
//            @Override public void run() {
//
//                int currentProgress = trackProgressSlider.getProgress() / 1000;
//                final int currentMinutes = currentProgress / 60;
//                final int currentSeconds = currentProgress % 60;
//
//                if (getActivity() == null) return;
//
//                getActivity().runOnUiThread(new Runnable() {
//
//                    @Override public void run() {
//                        try {
//                            if (trackProgressSlider == null) return;
//                            trackProgressSlider.setProgress(trackProgressSlider.getProgress() + 1000);
//                            trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
//                        } catch (Exception ex) {
//                            if (BuildConfig.DEBUG) {
//                                Log.d("mbrc-log:","animation timer", ex);
//                            }
//                        }
//                    }
//                });
//            }
//        };
//
//        mProgressUpdateHandler = progressScheduler.scheduleAtFixedRate(updateProgress, 0,
//                TIME_PERIOD, TimeUnit.SECONDS);

    }

    private void activateStoppedState() {
//        if (trackProgressCurrent == null || trackProgressSlider == null || stopButton == null) return;
//        trackProgressSlider.setProgress(0);
//        trackProgressCurrent.setText("00:00");
//        stopButton.setEnabled(false);
    }

    @Subscribe public void handleTrackInfoChange(final TrackInfoChange change) {
//        if (artistLabel == null) return;
//        artistLabel.setText(change.getArtist());
//        titleLabel.setText(change.getTitle());
//        albumLabel.setText(change.getAlbum());
//        yearLabel.setText(change.getYear());
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(change.getTitle());
        actionBar.setSubtitle(change.getAlbum());
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Subscribe public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        if (change.getStatus() == ConnectionStatus.CONNECTION_OFF) {
            stopTrackProgressAnimation();
            activateStoppedState();
        }
    }

    @Subscribe public void handleLfmStatusChange(final LfmRatingChanged event) {
        switch (event.getStatus()) {
//            case LOVED:
//                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_loved);
//                break;
//            case BANNED:
//                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_banned);
//                break;
//            case NORMAL:
//                lfmLoveButton.setImageResource(R.drawable.ic_media_lfm_unloved);
//                break;
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
