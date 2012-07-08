package kelsos.mbremote.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.inject.Inject;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Events.UserAction;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.Models.PlayState;
import kelsos.mbremote.R;
import roboguice.activity.RoboActivity;
import roboguice.event.EventManager;
import roboguice.inject.InjectView;

import java.util.Timer;
import java.util.TimerTask;

public class MainView extends RoboActivity {
    // Inject elements of the view
    @InjectView(R.id.artistLabel) TextView artistLabel;
    @InjectView(R.id.titleLabel) TextView titleLabel;
    @InjectView(R.id.albumLabel) TextView albumLabel;
    @InjectView(R.id.yearLabel) TextView yearLabel;
    @InjectView(R.id.trackProgressCurrent) TextView trackProgressCurrent;
    @InjectView(R.id.trackDuration) TextView trackDuration;
    @InjectView(R.id.playPauseButton) ImageButton playPauseButton;
    @InjectView(R.id.previousButton) ImageButton previousButton;
    @InjectView(R.id.nextButton) ImageButton nextButton;
    @InjectView(R.id.volumeSlider) SeekBar volumeSlider;
    @InjectView(R.id.trackProgressSlider) SeekBar trackProgressSlider;
    @InjectView(R.id.stopButton) ImageButton stopButton;
    @InjectView(R.id.muteButton) ImageButton muteButton;
    @InjectView(R.id.scrobbleButton) ImageButton scrobbleButton;
    @InjectView(R.id.shuffleButton) ImageButton shuffleButton;
    @InjectView(R.id.repeatButton) ImageButton repeatButton;
    @InjectView(R.id.connectivityIndicator) ImageButton connectivityIndicator;
    @InjectView(R.id.albumCover) ImageView albumCover;

    // Injects
    @Inject protected EventManager userActionEvent;
    @Inject private Controller controller;

    private boolean userChangingVolume;
    private Timer progressUpdateTimer_;
    private TimerTask progressUpdateTask_;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        RegisterListeners();
        userChangingVolume = false;
        SetTextViewTypeface();
        controller.onActivityStart(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onActivityStart(this);
    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void SetTextViewTypeface() {
        /* Marquee Hack */
        artistLabel.setSelected(true);
        titleLabel.setSelected(true);
        albumLabel.setSelected(true);
        yearLabel.setSelected(true);

        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        Typeface myriadPro = Typeface.createFromAsset(getAssets(), "fonts/myriadproregular.ttf");
        artistLabel.setTypeface(robotoLight);
        titleLabel.setTypeface(robotoLight);
        albumLabel.setTypeface(robotoLight);
        yearLabel.setTypeface(robotoLight);
        trackProgressCurrent.setTypeface(myriadPro);
        trackDuration.setTypeface(myriadPro);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                Intent settingsIntent = new Intent(MainView.this, AppPreferenceView.class);
                startActivity(settingsIntent);
                break;
            case R.id.main_menu_playlist:
                Intent playlistIntent = new Intent(MainView.this, PlaylistView.class);
                startActivity(playlistIntent);
            case R.id.main_menu_lyrics:
                userActionEvent.fire(new UserActionEvent(this, UserAction.Lyrics));
            default:
                return super.onMenuItemSelected(featureId, item);
        }
        return true;

    }


    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
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

    }

    /**
     * Given a boolean state this function updates the Scrobbler button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state If true it means that the scrobbler is active, false is used for inactive.
     */
    public void updateScrobblerButtonState(boolean state) {
        if (state) {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_red);
        } else {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_off);
        }
    }

    public void updateAlbumCover(Bitmap cover) {
        albumCover.setImageBitmap(cover);
    }

    /**
     * Given a boolean state value this function updates the Shuffle button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state True is used to represent active shuffle, false is used for inactive.
     */
    public void updateShuffleButtonState(boolean state) {
        if (state) {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
        } else {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle_off);
        }
    }

    public void updateRepeatButtonState(boolean state) {
        if (state) {
            repeatButton.setImageResource(R.drawable.ic_media_repeat);
        } else {
            repeatButton.setImageResource(R.drawable.ic_media_repeat_off);
        }
    }

    public void updateMuteButtonState(boolean state) {
        if (state) {
            muteButton.setImageResource(R.drawable.ic_media_mute_active);
        } else {
            muteButton.setImageResource(R.drawable.ic_media_mute_full);
        }
    }

    public void updateVolumeData(int volume) {
        if (!userChangingVolume)
            volumeSlider.setProgress(volume);
    }

    public void updatePlayState(PlayState playState) {
        switch (playState) {
            case Playing:
                playPauseButton.setImageResource(R.drawable.ic_media_pause);
                stopButton.setImageResource(R.drawable.ic_media_stop);
                stopButton.setEnabled(true);

                /* Start the animation if the track is playing*/
                trackProgressAnimation();
                break;
            case Paused:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                stopButton.setEnabled(true);
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                trackProgressSlider.setProgress(0);
                trackProgressCurrent.setText("00:00");
            case Undefined:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                stopButton.setImageResource(R.drawable.ic_media_stop_pressed);
                stopButton.setEnabled(false);
                break;
        }
    }

    public void updateArtistText(String artist) {
        artistLabel.setText(artist);
    }

    public void updateTitleText(String title) {
        titleLabel.setText(title);
    }

    public void updateAlbumText(String album) {
        albumLabel.setText(album);
    }

    public void updateYearText(String year) {
        yearLabel.setText(year);
    }

    public void updateConnectionIndicator(boolean connected) {
        if (connected) {
            connectivityIndicator.setImageResource(R.drawable.ic_icon_indicator_green);
        } else {
            connectivityIndicator.setImageResource(R.drawable.ic_icon_indicator_red);
        }
    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     * @param current Integer represents the current playback position in milliseconds
     * @param total Integer represents the total track duration in milliseconds
     */
    public void updateDurationDisplay(int current, int total)
    {
        int currentSeconds = current/1000;
        int totalSeconds = total/1000;

        int currentMinutes = currentSeconds/60;
        int totalMinutes = totalSeconds/60;

        currentSeconds %= 60;
        totalSeconds %= 60;

        trackDuration.setText(String.format("%02d:%02d", totalMinutes, totalSeconds));
        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));

        trackProgressSlider.setMax(total);
        trackProgressSlider.setProgress(current);

        trackProgressAnimation();
    }

    /**
     * Starts the progress animation when called. If It was previously running then it restarts it.
     */
    private void trackProgressAnimation() {
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        final int timerPeriod = 100;
        stopTrackProgressAnimation();
        progressUpdateTimer_ = new Timer(true);
        progressUpdateTask_ = new TimerTask() {
            @Override
            public void run() {
                int currentProgress = trackProgressSlider.getProgress()/1000;
                final int currentMinutes = currentProgress/60;
                final int currentSeconds = currentProgress%60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trackProgressSlider.setProgress(trackProgressSlider.getProgress()+timerPeriod);
                        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                    }
                });

            }
        };
        progressUpdateTimer_.schedule(progressUpdateTask_, 0, timerPeriod);
    }

    /**
     * If the track progress animation is running the the function stops it.
     */
    private void stopTrackProgressAnimation() {
        if(progressUpdateTask_!=null)
        {
            progressUpdateTask_.cancel();
            progressUpdateTask_ = null;
            progressUpdateTimer_.cancel();
            progressUpdateTimer_.purge();
            progressUpdateTimer_ = null;
        }
    }

    private OnClickListener playButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.PlayPause));
        }
    };

    private OnClickListener previousButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Previous));
        }
    };

    private OnClickListener nextButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Next));
        }
    };

    private OnClickListener stopButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Stop));
        }
    };

    private OnClickListener muteButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Mute));
        }
    };

    private OnClickListener scrobbleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Scrobble));
        }
    };

    private OnClickListener shuffleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Shuffle));
        }
    };

    private OnClickListener repeatButtonListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Repeat));
        }
    };
    private OnClickListener connectivityIndicatorListener = new OnClickListener() {

        public void onClick(View v) {
            userActionEvent.fire(new UserActionEvent(this, UserAction.Initialize));
        }
    };

    private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
            userChangingVolume = false;

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            userChangingVolume = true;

        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
                userActionEvent.fire(new UserActionEvent(this, UserAction.Volume, String.valueOf(seekBar.getProgress())));
        }
    };

    private OnSeekBarChangeListener durationSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
            {
                userActionEvent.fire(new UserActionEvent(this, UserAction.PlaybackPosition, String.valueOf(progress)));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            ||
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ||
        }
    };

}