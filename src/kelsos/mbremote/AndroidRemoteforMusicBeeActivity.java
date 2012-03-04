package kelsos.mbremote;

import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;
import kelsos.mbremote.Network.ReplyHandler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidRemoteforMusicBeeActivity extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	private ConnectivityHandler mBoundService;
	private boolean mIsBound;
	private boolean userChangingVolume;
	private static final String TOGGLE = "toggle";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		RegisterListeners();
		startService(new Intent(AndroidRemoteforMusicBeeActivity.this,
				ConnectivityHandler.class));
		doBindService();
		registerIntentFilters();
		userChangingVolume = false;
		LinearLayout layout = (LinearLayout) findViewById(R.id.playingTrackLayout);
		registerForContextMenu(layout);

	}

	private void registerIntentFilters() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.VOLUME_DATA);
		filter.addAction(Intents.SONG_DATA);
		filter.addAction(Intents.SONG_COVER);
		filter.addAction(Intents.PLAY_STATE);
		filter.addAction(Intents.MUTE_STATE);
		filter.addAction(Intents.SCROBBLER_STATE);
		filter.addAction(Intents.REPEAT_STATE);
		filter.addAction(Intents.SHUFFLE_STATE);
		filter.addAction(Intents.LYRICS_DATA);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Actions");
		// menu.add(0, v.getId(), 0, "Rating");
		menu.add(0, v.getId(), 0, "Lyrics");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().equals("Lyrics")) {
			mBoundService.requestHandler().requestAction(PlayerAction.Lyrics);
		} else if (item.getTitle().equals("Rating")) {

		} else {
			return false;
		}
		return true;
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
			Intent settingsIntent = new Intent(
					AndroidRemoteforMusicBeeActivity.this, AppSettings.class);
			startActivity(settingsIntent);
			break;
		case R.id.main_menu_playlist:
			Intent playlistIntent = new Intent(
					AndroidRemoteforMusicBeeActivity.this,
					PlaylistActivity.class);
			startActivity(playlistIntent);
		case R.id.main_menu_connect:
			mBoundService
					.attemptToStartSocketThread(ConnectivityHandler.Input.user);
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
		scrobbleButton.setOnClickListener(scrobbleButtonListener);
		ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
		shuffleButton.setOnClickListener(shuffleButtonListener);
		ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
		repeatButton.setOnClickListener(repeatButtonListener);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intents.VOLUME_DATA)) {
				SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
				if (!userChangingVolume)
					volumeSlider.setProgress(intent.getExtras().getInt("data"));
			} else if (intent.getAction().equals(Intents.SONG_DATA)) {
				TextView artistTextView = (TextView) findViewById(R.id.artistLabel);
				TextView titleTextView = (TextView) findViewById(R.id.titleLabel);
				TextView albumTextView = (TextView) findViewById(R.id.albumLabel);
				TextView yearTextView = (TextView) findViewById(R.id.yearLabel);

				artistTextView.setText(intent.getExtras().getString("artist"));
				titleTextView.setText(intent.getExtras().getString("title"));
				albumTextView.setText(intent.getExtras().getString("album"));
				yearTextView.setText(intent.getExtras().getString("year"));
			} else if (intent.getAction().equals(Intents.SONG_COVER)) {
				new ImageDecodeTask().execute();
			} else if (intent.getAction().equals(Intents.PLAY_STATE)) {
				if (intent.getExtras().getString("state").equals("playing")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_pause);
				} else if (intent.getExtras().getString("state")
						.equals("paused")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_play);
				} else if (intent.getExtras().getString("state")
						.equals("stopped")) {
					ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
					playButton.setImageResource(R.drawable.ic_media_play);
				}
			} else if (intent.getAction().equals(Intents.MUTE_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("True")) {
					ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
					muteButton.setImageResource(R.drawable.ic_media_volume_off);
				} else {
					ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
					muteButton
							.setImageResource(R.drawable.ic_media_volume_full);
				}
			} else if (intent.getAction().equals(Intents.REPEAT_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("All")) {
					ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
					repeatButton.setImageResource(R.drawable.ic_media_repeat);
				} else {
					ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
					repeatButton
							.setImageResource(R.drawable.ic_media_repeat_off);
				}
			} else if (intent.getAction().equals(Intents.SHUFFLE_STATE)) {
				if (intent.getExtras().getString("state")
						.equalsIgnoreCase("True")) {
					ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
					shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
				} else {
					ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
					shuffleButton
							.setImageResource(R.drawable.ic_media_shuffle_off);
				}
			} else if (intent.getAction().equals(Intents.SCROBBLER_STATE)) {
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
			} else if (intent.getAction().equals(Intents.LYRICS_DATA)) {
				if (ReplyHandler.getInstance().getSongLyrics().equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.no_lyrics_found, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				LayoutInflater layoutInflater = (LayoutInflater) AndroidRemoteforMusicBeeActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final PopupWindow lyricsPopup = new PopupWindow(
						layoutInflater.inflate(R.layout.popup_lyrics, null,
								false),
						((WindowManager) getSystemService(Context.WINDOW_SERVICE))
								.getDefaultDisplay().getWidth(),
						((WindowManager) getSystemService(Context.WINDOW_SERVICE))
								.getDefaultDisplay().getHeight() - 30, true);
				lyricsPopup.setOutsideTouchable(true);
				((TextView) lyricsPopup.getContentView().findViewById(
						R.id.lyricsLabel))
						.setText("Lyrics for "
								+ ((TextView) findViewById(R.id.titleLabel))
										.getText()
								+ "\nby "
								+ ((TextView) findViewById(R.id.artistLabel))
										.getText());

				((TextView) lyricsPopup.getContentView().findViewById(
						R.id.lyricsText)).setText(ReplyHandler.getInstance()
						.getSongLyrics());
				lyricsPopup.getContentView()
						.findViewById(R.id.popup_close_button)
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								lyricsPopup.dismiss();

							}
						});
				lyricsPopup.showAtLocation(findViewById(R.id.mainLinearLayout),
						Gravity.CENTER, 0, 0);
				ReplyHandler.getInstance().clearLyrics();
			}
		}
	};

	private class ImageDecodeTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			byte[] decodedImage = Base64.decode(ReplyHandler.getInstance()
					.getCoverData(), Base64.DEFAULT);
			return BitmapFactory.decodeByteArray(decodedImage, 0,
					decodedImage.length);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ImageView cover = (ImageView) findViewById(R.id.albumCover);
			cover.setImageBitmap(result);
			ReplyHandler.getInstance().clearCoverData();
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((ConnectivityHandler.LocalBinder) service)
					.getService();
		}
	};

	void doBindService() {
		bindService(new Intent(AndroidRemoteforMusicBeeActivity.this,
				ConnectivityHandler.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
			mBoundService.requestHandler()
					.requestAction(PlayerAction.PlayPause);
		}
	};

	private OnClickListener previousButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Previous);
		}
	};

	private OnClickListener nextButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Next);
		}
	};

	private OnClickListener stopButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Stop);
		}
	};

	private OnClickListener muteButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Mute,
					TOGGLE);
		}
	};

	private OnClickListener scrobbleButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Scrobble,
					TOGGLE);

		}
	};

	private OnClickListener shuffleButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Shuffle,
					TOGGLE);
		}
	};

	private OnClickListener repeatButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Repeat,
					TOGGLE);
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
				mBoundService.requestHandler().requestAction(
						PlayerAction.Volume,
						Integer.toString(seekBar.getProgress()));
		}
	};

}