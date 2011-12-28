package kelsos.mbremote;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import kelsos.mbremote.Network.AnswerHandler;
import kelsos.mbremote.Network.NetworkManager;

public class AndroidRemoteforMusicBeeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private NetworkManager mBoundService;
    private boolean mIsBound;
    private boolean userChangingVolume;
    private static final String TOGGLE = "toggle";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(AndroidRemoteforMusicBeeActivity.this,
                NetworkManager.class));
        doBindService();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AnswerHandler.VOLUME_DATA);
        filter.addAction(AnswerHandler.SONG_DATA);
        filter.addAction(AnswerHandler.SONG_COVER);
        registerReceiver(mReceiver, filter);
        // Buttons and listeners
        ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
        playButton.setOnClickListener(playButtonListener);
        ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(previousButtonListener);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
        volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(stopButtonListener);
        ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(muteButtonListener);
        ImageButton scrobbleButton = (ImageButton) findViewById(R.id.scrobbleButton);
        scrobbleButton.setOnClickListener(scrobbleButtonListerner);
        ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
        shuffleButton.setOnClickListener(shuffleButtonListener);
        ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
        repeatButton.setOnClickListener(repeatButtonListener);

        userChangingVolume = false;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AnswerHandler.VOLUME_DATA)) {
                SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
                if (!userChangingVolume)
                    volumeSlider.setProgress(intent.getExtras().getInt("data"));
            }
            if (intent.getAction().equals(AnswerHandler.SONG_DATA)) {
                TextView artistTextView = (TextView) findViewById(R.id.artistLabel);
                TextView titleTextView = (TextView) findViewById(R.id.titleLabel);
                TextView albumTextView = (TextView) findViewById(R.id.albumLabel);
                TextView yearTextView = (TextView) findViewById(R.id.yearLabel);

                artistTextView.setText(intent.getExtras().getString("artist"));
                titleTextView.setText(intent.getExtras().getString("title"));
                albumTextView.setText(intent.getExtras().getString("album"));
                yearTextView.setText(intent.getExtras().getString("year"));
            }
            if (intent.getAction().equals(AnswerHandler.SONG_COVER)) {
                new ImageDecodeTask().execute();
            }
            Log.d("Intent:", intent.getAction());
        }
    };

    private class ImageDecodeTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            byte[] decodedImage = Base64.decode(mBoundService.getCoverData(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView cover = (ImageView) findViewById(R.id.albumCover);
            cover.setImageBitmap(result);
            mBoundService.clearCoverData();
            Log.d("Cover:", "Cover - Updated");
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((NetworkManager.LocalBinder) service).getService();
        }
    };

    void doBindService() {
        bindService(new Intent(AndroidRemoteforMusicBeeActivity.this,
                NetworkManager.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        unregisterReceiver(mReceiver);
    }

    private OnClickListener playButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestPlayPause();
        }
    };
    private OnClickListener previousButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestPreviousTrack();
        }
    };

    private OnClickListener nextButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestNextTrack();
        }
    };

    private OnClickListener stopButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestStopPlayback();
        }
    };

    private OnClickListener muteButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestMuteState(TOGGLE);

        }
    };

    private OnClickListener scrobbleButtonListerner = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestScrobblerState(TOGGLE);

        }
    };

    private OnClickListener shuffleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestShuffleState(TOGGLE);
        }
    };

    private OnClickListener repeatButtonListener = new OnClickListener() {

        public void onClick(View v) {
            mBoundService.requestRepeatState(TOGGLE);
        }
    };

    private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
            userChangingVolume = false;

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            userChangingVolume = true;

        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser)
                mBoundService.requestVolumeChange(seekBar.getProgress());
        }
    };
}