package kelsos.mbremote.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Events.UserAction;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.Events.UserActionEventListener;
import kelsos.mbremote.Events.UserActionEventSource;
import kelsos.mbremote.Models.PlayState;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainView extends Activity {
    private DelayTimer _selfRegistrationTimer;
    private static final String BY = "\nby ";
    private static final String LYRICS_FOR = "Lyrics for ";
    private boolean userChangingVolume;
    private UserActionEventSource _userUserActionEventSource;
    private Timer progressUpdateTimer_;
    private TimerTask progressUpdateTask_;

    DelayTimer.TimerFinishEvent selfRegistrationTimerEvent = new DelayTimer.TimerFinishEvent() {
        @Override
        public void onTimerFinish() {
            registerSelf();
        }
    };

    /**
     * This function is used to register self in the Controller service.
     */
    private void registerSelf() {
        Controller.getInstance().initialize(this);
    }

    public void addEventListener(UserActionEventListener listener) {
        _userUserActionEventSource.addEventListener(listener);
    }

    public void removeEventListener(UserActionEventListener listener) {
        _userUserActionEventSource.removeEventListener(listener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        _userUserActionEventSource = new UserActionEventSource();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        RegisterListeners();
        userChangingVolume = false;
        SetTextViewTypeface();
        /* If the controller is not running send the start intent */
        if (!Controller.getIsRunning()) {
            startService(new Intent(this, Controller.class));
            _selfRegistrationTimer = new DelayTimer(2000);
            _selfRegistrationTimer.setTimerFinishEventListener(selfRegistrationTimerEvent);
            _selfRegistrationTimer.start();
        } else {
            Controller.getInstance().onActivityStart(this);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Controller.getIsRunning()) {
            Controller.getInstance().onActivityStart(this);
        }
    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void SetTextViewTypeface() {
        /* Marquee Hack */
        getTextViewById(R.id.artistLabel).setSelected(true);
        getTextViewById(R.id.titleLabel).setSelected(true);
        getTextViewById(R.id.albumLabel).setSelected(true);
        getTextViewById(R.id.yearLabel).setSelected(true);

        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        Typeface myriadPro = Typeface.createFromAsset(getAssets(), "fonts/myriadproregular.ttf");
        getTextViewById(R.id.artistLabel).setTypeface(robotoLight);
        getTextViewById(R.id.titleLabel).setTypeface(robotoLight);
        getTextViewById(R.id.albumLabel).setTypeface(robotoLight);
        getTextViewById(R.id.yearLabel).setTypeface(robotoLight);
        getTextViewById(R.id.trackProgressCurrent).setTypeface(myriadPro);
        getTextViewById(R.id.trackDuration).setTypeface(myriadPro);
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
                _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Lyrics));
            default:
                return super.onMenuItemSelected(featureId, item);
        }
        return true;

    }

    private ImageButton getImageButtonById(int id) {
        return (ImageButton) findViewById(id);
    }

    /**
     * Finds and returns a SeekBar identified by an id.
     *
     * @param id Represents the id of a SeekBar.
     * @return The SeekBar that matches the id.
     */
    private SeekBar getSeekBarById(int id) {
        return (SeekBar) findViewById(id);
    }

    /**
     * Finds and returns a TextView identified by an id.
     *
     * @param id Represents a TextView
     * @return The TextView that matched the id.
     */
    private TextView getTextViewById(int id) {
        return (TextView) findViewById(id);
    }

    /**
     * Finds and returns an ImageView identified by an id.
     *
     * @param id Represents an ImageView.
     * @return The ImageView that matches the id provided.
     */
    private ImageView getImageViewById(int id) {
        return (ImageView) findViewById(id);
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        getImageButtonById(R.id.playPauseButton).setOnClickListener(playButtonListener);
        getImageButtonById(R.id.previousButton).setOnClickListener(previousButtonListener);
        getImageButtonById(R.id.nextButton).setOnClickListener(nextButtonListener);
        getSeekBarById(R.id.volumeSlider).setOnSeekBarChangeListener(volumeChangeListener);
        getSeekBarById(R.id.trackProgressSlider).setOnSeekBarChangeListener(durationSeekBarChangeListener);
        getImageButtonById(R.id.stopButton).setOnClickListener(stopButtonListener);
        getImageButtonById(R.id.stopButton).setEnabled(false);
        getImageButtonById(R.id.muteButton).setOnClickListener(muteButtonListener);
        getImageButtonById(R.id.scrobbleButton).setOnClickListener(scrobbleButtonListener);
        getImageButtonById(R.id.shuffleButton).setOnClickListener(shuffleButtonListener);
        getImageButtonById(R.id.repeatButton).setOnClickListener(repeatButtonListener);
        getImageButtonById(R.id.connectivityIndicator).setOnClickListener(connectivityIndicatorListener);

    }

    /**
     * Given a boolean state this function updates the Scrobbler button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state If true it means that the scrobbler is active, false is used for inactive.
     */
    public void updateScrobblerButtonState(boolean state) {
        if (state) {
            getImageButtonById(R.id.scrobbleButton).setImageResource(R.drawable.ic_media_scrobble_red);
        } else {
            getImageButtonById(R.id.scrobbleButton).setImageResource(R.drawable.ic_media_scrobble_off);
        }
    }

    public void updateAlbumCover(Bitmap cover) {
        getImageViewById(R.id.albumCover).setImageBitmap(cover);
    }

    /**
     * Given a boolean state value this function updates the Shuffle button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state True is used to represent active shuffle, false is used for inactive.
     */
    public void updateShuffleButtonState(boolean state) {
        if (state) {
            getImageButtonById(R.id.shuffleButton).setImageResource(R.drawable.ic_media_shuffle);
        } else {
            getImageButtonById(R.id.shuffleButton).setImageResource(R.drawable.ic_media_shuffle_off);
        }
    }

    public void updateRepeatButtonState(boolean state) {
        if (state) {
            getImageButtonById(R.id.repeatButton).setImageResource(R.drawable.ic_media_repeat);
        } else {
            getImageButtonById(R.id.repeatButton).setImageResource(R.drawable.ic_media_repeat_off);
        }
    }

    public void updateMuteButtonState(boolean state) {
        if (state) {
            getImageButtonById(R.id.muteButton).setImageResource(R.drawable.ic_media_mute_active);
        } else {
            getImageButtonById(R.id.muteButton).setImageResource(R.drawable.ic_media_mute_full);
        }
    }

    public void updateVolumeData(int volume) {
        if (!userChangingVolume)
            getSeekBarById(R.id.volumeSlider).setProgress(volume);
    }

    public void updatePlayState(PlayState playState) {
        switch (playState) {
            case Playing:
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_pause);
                getImageButtonById(R.id.stopButton).setImageResource(R.drawable.ic_media_stop);
                getImageButtonById(R.id.stopButton).setEnabled(true);

                /* Start the animation if the track is playing*/
                trackProgressAnimation();
                break;
            case Paused:
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_play);
                getImageButtonById(R.id.stopButton).setEnabled(true);
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                getSeekBarById(R.id.trackProgressSlider).setProgress(0);
                getTextViewById(R.id.trackProgressCurrent).setText("00:00");
            case Undefined:
                getImageButtonById(R.id.playPauseButton).setImageResource(R.drawable.ic_media_play);
                getImageButtonById(R.id.stopButton).setImageResource(R.drawable.ic_media_stop_pressed);
                getImageButtonById(R.id.stopButton).setEnabled(false);
                break;
        }
    }

    public void updateArtistText(String artist) {
        getTextViewById(R.id.artistLabel).setText(artist);
    }

    public void updateTitleText(String title) {
        getTextViewById(R.id.titleLabel).setText(title);
    }

    public void updateAlbumText(String album) {
        getTextViewById(R.id.albumLabel).setText(album);
    }

    public void updateYearText(String year) {
        getTextViewById(R.id.yearLabel).setText(year);
    }

    public void updateConnectionIndicator(boolean connected) {
        if (connected) {
            getImageViewById(R.id.connectivityIndicator).setImageResource(R.drawable.ic_icon_indicator_green);
        } else {
            getImageViewById(R.id.connectivityIndicator).setImageResource(R.drawable.ic_icon_indicator_red);
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

        getTextViewById(R.id.trackDuration).setText(String.format("%02d:%02d",totalMinutes,totalSeconds));
        getTextViewById(R.id.trackProgressCurrent).setText(String.format("%02d:%02d",currentMinutes,currentSeconds));

        getSeekBarById(R.id.trackProgressSlider).setMax(total);
        getSeekBarById(R.id.trackProgressSlider).setProgress(current);

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
                int currentProgress = getSeekBarById(R.id.trackProgressSlider).getProgress()/1000;
                final int currentMinutes = currentProgress/60;
                final int currentSeconds = currentProgress%60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSeekBarById(R.id.trackProgressSlider).setProgress(getSeekBarById(R.id.trackProgressSlider).getProgress()+timerPeriod);
                        getTextViewById(R.id.trackProgressCurrent).setText(String.format("%02d:%02d",currentMinutes,currentSeconds));
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

    /**
     * When this function is called is either displaying a "No Lyrics found" toast
     * message or it displays a popup with the lyrics of the track.
     */
    private void processLyricsData() {
//        if (ReplyHandler.getInstance().getSongLyrics().equals("")) {
//            NotificationService.getInstance().showToastMessage(R.string.no_lyrics_found);
//            return;
//        }
        LayoutInflater layoutInflater = (LayoutInflater) MainView.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int windowWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();

        final PopupWindow lyricsPopup = new PopupWindow(layoutInflater.inflate(
                R.layout.popup_lyrics, null, false), windowWidth,
                windowHeight - 30, true);
        lyricsPopup.setOutsideTouchable(true);

        CharSequence artist = getTextViewById(R.id.artistLabel).getText();
        CharSequence title = getTextViewById(R.id.titleLabel).getText();

        ((TextView) lyricsPopup.getContentView().findViewById(R.id.lyricsLabel))
                .setText(LYRICS_FOR + title + BY + artist);

//        ((TextView) lyricsPopup.getContentView().findViewById(R.id.lyricsText))
//                .setText(ReplyHandler.getInstance().getSongLyrics());
        lyricsPopup.getContentView().findViewById(R.id.popup_close_button)
                .setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        lyricsPopup.dismiss();

                    }
                });
        lyricsPopup.showAtLocation(findViewById(R.id.mainLinearLayout),
                Gravity.CENTER, 0, 0);
//        ReplyHandler.getInstance().clearLyrics();
    }

    private OnClickListener playButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.PlayPause));
        }
    };

    private OnClickListener previousButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Previous));
        }
    };

    private OnClickListener nextButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Next));
        }
    };

    private OnClickListener stopButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Stop));
        }
    };

    private OnClickListener muteButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Mute));
        }
    };

    private OnClickListener scrobbleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Scrobble));
        }
    };

    private OnClickListener shuffleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Shuffle));
        }
    };

    private OnClickListener repeatButtonListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Repeat));
        }
    };
    private OnClickListener connectivityIndicatorListener = new OnClickListener() {

        public void onClick(View v) {
            _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Initialize));
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
                _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.Volume, String.valueOf(seekBar.getProgress())));
        }
    };

    private OnSeekBarChangeListener durationSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
            {
                _userUserActionEventSource.dispatchEvent(new UserActionEvent(this, UserAction.PlaybackPosition, String.valueOf(progress)));
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