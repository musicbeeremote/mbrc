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
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
		filter.addAction(AnswerHandler.PLAY_STATE);
		filter.addAction(AnswerHandler.MUTE_STATE);
		filter.addAction(AnswerHandler.SCROBBLER_STATE);
		filter.addAction(AnswerHandler.REPEAT_STATE);
		filter.addAction(AnswerHandler.SHUFFLE_STATE);
		filter.addAction(AnswerHandler.LYRICS_DATA);
		registerReceiver(mReceiver, filter);
		RegisterListeners();
		userChangingVolume = false;
		LinearLayout layout = (LinearLayout) findViewById(R.id.playingTrackLayout);
		registerForContextMenu(layout);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle("Actions");
		menu.add(0, v.getId(), 0, "Rating");
		menu.add(0, v.getId(), 0, "Lyrics");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().equals("Lyrics")) {
			mBoundService.requestTrackLyrics();
		} else if (item.getTitle().equals("Rating")) {

		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_settings:
			Intent settingsIntent = new Intent(
					AndroidRemoteforMusicBeeActivity.this, AppSettings.class);
			startActivity(settingsIntent);
			break;
		case R.id.main_menu_playlist:
			Intent playlistIntent = new Intent(
					AndroidRemoteforMusicBeeActivity.this,
					PlaylistActivity.class);
			startActivity(playlistIntent);
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return true;

	}

	private void RegisterListeners() {
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
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AnswerHandler.VOLUME_DATA)) {
				SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
				if (!userChangingVolume)
					volumeSlider.setProgress(intent.getExtras().getInt("data"));
			} else if (intent.getAction().equals(AnswerHandler.SONG_DATA)) {
				TextView artistTextView = (TextView) findViewById(R.id.artistLabel);
				TextView titleTextView = (TextView) findViewById(R.id.titleLabel);
				TextView albumTextView = (TextView) findViewById(R.id.albumLabel);
				TextView yearTextView = (TextView) findViewById(R.id.yearLabel);

				artistTextView.setText(intent.getExtras().getString("artist"));
				titleTextView.setText(intent.getExtras().getString("title"));
				albumTextView.setText(intent.getExtras().getString("album"));
				yearTextView.setText(intent.getExtras().getString("year"));
			} else if (intent.getAction().equals(AnswerHandler.SONG_COVER)) {
				new ImageDecodeTask().execute();
			} else if (intent.getAction().equals(AnswerHandler.PLAY_STATE)) {
				if (intent.getExtras().getString("playstate").equals("playing")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_pause);
				} else if (intent.getExtras().getString("playstate")
						.equals("paused")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_play);
				} else if (intent.getExtras().getString("playstate")
						.equals("stopped")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_play);
				}
			} else if (intent.getAction().equals(AnswerHandler.MUTE_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("True")) {
					ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
					muteButton.setImageResource(R.drawable.ic_media_volume_off);
				} else {
					ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
					muteButton
							.setImageResource(R.drawable.ic_media_volume_full);
				}
			} else if (intent.getAction().equals(AnswerHandler.REPEAT_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("All")) {
					ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
					repeatButton.setImageResource(R.drawable.ic_media_repeat);
				} else {
					ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
					repeatButton
							.setImageResource(R.drawable.ic_media_repeat_off);
				}
			} else if (intent.getAction().equals(AnswerHandler.SHUFFLE_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("True")) {
					ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
					shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
				} else {
					ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
					shuffleButton
							.setImageResource(R.drawable.ic_media_shuffle_off);
				}
			} else if (intent.getAction().equals(AnswerHandler.SCROBBLER_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("True")) {
					ImageButton scrobbleButton = (ImageButton) findViewById(R.id.scrobbleButton);
					scrobbleButton
							.setImageResource(R.drawable.ic_media_scrobble_red);
				} else {
					ImageButton scrobbleButton = (ImageButton) findViewById(R.id.scrobbleButton);
					scrobbleButton
							.setImageResource(R.drawable.ic_media_scrobble_off);
				}
			} else if (intent.getAction().equals(AnswerHandler.LYRICS_DATA)) {
				LayoutInflater popupInflater = (LayoutInflater)AndroidRemoteforMusicBeeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final PopupWindow lyricsPopup = new PopupWindow(popupInflater.inflate(R.layout.popup_lyrics, null, false),((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(),((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()-30,true);
				lyricsPopup.setOutsideTouchable(true);
				((TextView)lyricsPopup.getContentView().findViewById(R.id.lyricsLabel)).setText("Lyrics for " + ((TextView)findViewById(R.id.titleLabel)).getText() + "\nby " + ((TextView)findViewById(R.id.artistLabel)).getText());
				
				((TextView)lyricsPopup.getContentView().findViewById(R.id.lyricsText)).setText(AnswerHandler.getInstance().getSongLyrics());;
				lyricsPopup.getContentView().findViewById(R.id.popup_close_button).setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						lyricsPopup.dismiss();
						
					}
				});
				lyricsPopup.showAtLocation(findViewById(R.id.mainLinearLayout), Gravity.CENTER, 0, 0);
				AnswerHandler.getInstance().clearLyrics();
			}
			// Log.d("Intent:", intent.getAction());
		}
	};

	private class ImageDecodeTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			byte[] decodedImage = Base64.decode(mBoundService.getCoverData(),
					Base64.DEFAULT);
			return BitmapFactory.decodeByteArray(decodedImage, 0,
					decodedImage.length);
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