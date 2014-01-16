package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.InfoButtonPagerAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.LinePageIndicator;
import roboguice.inject.InjectView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class MainFragment extends BaseFragment {
    /**
     * Total milliseconds in a second (1000)
     */
    public static final int MILLISECONDS = 1000;
    /**
     * Total seconds in a minute (60)
     */
    public static final int SECONDS = 60;
    @InjectView(R.id.main_track_progress_current) private TextView trackProgressCurrent;
    @InjectView(R.id.main_track_duration_total) private TextView trackDuration;
    @InjectView(R.id.main_volume_seeker) private SeekBar volumeSlider;
    @InjectView(R.id.main_track_progress_seeker) private SeekBar trackProgressSlider;
    @InjectView(R.id.main_album_cover_image_view) private ImageView albumCover;
    @InjectView(R.id.ratingWrapper) private LinearLayout ratingWrapper;
    @InjectView(R.id.track_rating_bar) private RatingBar trackRating;

    private ShareActionProvider mShareActionProvider;
    private int previousVol;
    private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mProgressUpdateHandler;
    private InfoButtonPagerAdapter mAdapter;

    private RatingBar.OnRatingBarChangeListener ratingChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (b) {
                post(new UserAction(Protocol.NOW_PLAYING_RATING, v));
            }
        }
    };
    public static final int TIME_PERIOD = 1;

    private void post(UserAction data) {
        getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, data));
    }

    private SeekBar.OnSeekBarChangeListener volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                post(new UserAction(Protocol.PLAYER_VOLUME, String.valueOf(seekBar.getProgress())));
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) { }

        public void onStartTrackingTouch(SeekBar seekBar) { }

    };
    private SeekBar.OnSeekBarChangeListener durationSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && progress != previousVol) {
                post(new UserAction(Protocol.NOW_PLAYING_POSITION, String.valueOf(progress)));
                previousVol = progress;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) { }

        public void onStopTrackingTouch(SeekBar seekBar) { }

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

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new InfoButtonPagerAdapter(getChildFragmentManager());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_main, container, false);
        ViewPager mPager = (ViewPager) view.findViewById(R.id.mbrc_main_infopager);
        mPager.setAdapter(mAdapter);
        LinePageIndicator mIndicator = (LinePageIndicator) view.findViewById(R.id.mbrc_main_infoindicator);
        mIndicator.setViewPager(mPager);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        setTextViewTypeface();
        registerListeners();
    }

    @Override public void onResume() {
        super.onResume();
        post(new UserAction(Protocol.NOW_PLAYING_POSITION, true));
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void registerListeners() {
        try {
            ratingWrapper.setVisibility(View.INVISIBLE);
            trackRating.setOnRatingBarChangeListener(ratingChangeListener);
            volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
            trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
            albumCover.setOnClickListener(coverOnClick);
        } catch (Exception ignore) { }

    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void setTextViewTypeface() {
        try {
            Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
            trackProgressCurrent.setTypeface(robotoRegular);
            trackDuration.setTypeface(robotoRegular);
        } catch (Exception ignore) { }
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
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Subscribe public void handleRatingChange(RatingChanged event) {
        if (trackRating != null) {
            trackRating.setRating(event.getRating());
        }
    }

    @Subscribe public void handleCoverEvent(final CoverAvailable cevent) {
        if (albumCover == null) {
            return;
        }
        if (cevent.getIsAvailable()) {
            albumCover.setImageBitmap(cevent.getCover());
        } else {
            albumCover.setImageResource(R.drawable.ic_image_no_cover);
        }
    }

    @Subscribe public void handlePlayStateChange(final PlayStateChange change) {
        switch (change.getState()) {
            case Playing:
                /* Start the animation if the track is playing*/
                post(new UserAction(Protocol.NOW_PLAYING_POSITION, true));
                trackProgressAnimation();
                break;
            case Paused:
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
        /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                activateStoppedState();
                break;
            case Undefined:
                stopTrackProgressAnimation();
                break;
            default:
                stopTrackProgressAnimation();
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
        if (!isVisible()){
            return;
        }
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        stopTrackProgressAnimation();

        final Runnable updateProgress = new Runnable() {
            @Override public void run() {

                int currentProgress = trackProgressSlider.getProgress() / MILLISECONDS;
                final int currentMinutes = currentProgress / SECONDS;
                final int currentSeconds = currentProgress % SECONDS;

                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {

                    @Override public void run() {
                        try {
                            if (trackProgressSlider == null) {
                                return;
                            }
                            trackProgressSlider.setProgress(trackProgressSlider.getProgress() + MILLISECONDS);
                            trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                        } catch (Exception ex) {

                            if (BuildConfig.DEBUG) {
                                Log.d("mbrc-log:", "animation timer", ex);
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
        if (trackProgressCurrent == null || trackProgressSlider == null) {
            return;
        }
        trackProgressSlider.setProgress(0);
        trackProgressCurrent.setText("00:00");
    }

    @Subscribe public void handleTrackInfoChange(final TrackInfoChange change) {
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

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     */
    @Subscribe public void handlePositionUpdate(UpdatePosition position) {
        final int total = position.getTotal();
        final int current = position.getCurrent();
        if (trackProgressCurrent == null || trackProgressSlider == null || trackDuration == null) {
            return;
        }
        if (total == 0) {
            getBus().post(new MessageEvent(UserInputEventType.REQUEST_POSITION));
            return;
        }
        int currentSeconds = current / MILLISECONDS;
        int totalSeconds = total / MILLISECONDS;

        final int currentMinutes = currentSeconds / SECONDS;
        final int totalMinutes = totalSeconds / SECONDS;

        currentSeconds %= SECONDS;
        totalSeconds %= SECONDS;
        final int finalTotalSeconds = totalSeconds;
        final int finalCurrentSeconds = currentSeconds;

        trackDuration.setText(String.format("%02d:%02d", totalMinutes, finalTotalSeconds));
        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, finalCurrentSeconds));

        trackProgressSlider.setMax(total);
        trackProgressSlider.setProgress(current);

        trackProgressAnimation();
    }
}
