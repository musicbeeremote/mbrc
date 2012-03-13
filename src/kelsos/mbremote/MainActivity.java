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

public class MainActivity extends Activity {
	private static final String BY = "\nby ";
	private static final String LYRICS_FOR = "Lyrics for ";
    /**
	 * Called when the activity is first created.
	 */
	private ConnectivityHandler mBoundService;
	private boolean mIsBound;
	private boolean userChangingVolume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		RegisterListeners();
		startService(new Intent(MainActivity.this, ConnectivityHandler.class));
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
		filter.addAction(Intents.CONNECTION_STATUS);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(AppConstants.ACTIONS);
		// menu.add(0, v.getId(), 0, "Rating");
		menu.add(0, v.getId(), 0, AppConstants.LYRICS);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().equals(AppConstants.LYRICS)) {
			mBoundService.requestHandler().requestAction(PlayerAction.Lyrics);
		} else if (item.getTitle().equals(AppConstants.RATING)) {

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
			Intent settingsIntent = new Intent(MainActivity.this,
					AppSettings.class);
			startActivity(settingsIntent);
			break;
		case R.id.main_menu_playlist:
			Intent playlistIntent = new Intent(MainActivity.this,
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

	private ImageButton getImageButtonById(int id) {
		ImageButton button = (ImageButton) findViewById(id);
		return button;
	}

	private SeekBar getSeekBarById(int id) {
		SeekBar seekBar = (SeekBar) findViewById(id);
		return seekBar;
	}

	private TextView getTextViewById(int id) {
		TextView textView = (TextView) findViewById(id);
		return textView;
	}

	private ImageView getImageViewById(int id) {
		ImageView imageView = (ImageView) findViewById(id);
		return imageView;
	}

	private void RegisterListeners() {
		getImageButtonById(R.id.playPauseButton).setOnClickListener(
				playButtonListener);
		getImageButtonById(R.id.previousButton).setOnClickListener(
				previousButtonListener);
		getImageButtonById(R.id.nextButton).setOnClickListener(
				nextButtonListener);
		getSeekBarById(R.id.volumeSlider).setOnSeekBarChangeListener(
				volumeChangeListener);
		getImageButtonById(R.id.stopButton).setOnClickListener(
				stopButtonListener);
		getImageButtonById(R.id.muteButton).setOnClickListener(
				muteButtonListener);
		getImageButtonById(R.id.scrobbleButton).setOnClickListener(
				scrobbleButtonListener);
		getImageButtonById(R.id.shuffleButton).setOnClickListener(
				shuffleButtonListener);
		getImageButtonById(R.id.repeatButton).setOnClickListener(
				repeatButtonListener);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intents.VOLUME_DATA)) {
				if (!userChangingVolume)
					getSeekBarById(R.id.volumeSlider).setProgress(
							intent.getExtras().getInt(AppConstants.DATA));
			} else if (intent.getAction().equals(Intents.CONNECTION_STATUS)) {
				boolean status = intent.getBooleanExtra(AppConstants.STATUS, false);
				if (status) {
					getImageViewById(R.id.imageView1).setImageResource(
							R.drawable.ic_icon_indicator_green);
				} else {
					getImageViewById(R.id.imageView1).setImageResource(
							R.drawable.ic_icon_indicator_red);
				}
			} else if (intent.getAction().equals(Intents.SONG_DATA)) {
				getTextViewById(R.id.artistLabel).setText(
						intent.getExtras().getString(AppConstants.ARTIST));
				getTextViewById(R.id.titleLabel).setText(
						intent.getExtras().getString(AppConstants.TITLE));
				getTextViewById(R.id.albumLabel).setText(
						intent.getExtras().getString(AppConstants.ALBUM));
				getTextViewById(R.id.yearLabel).setText(
						intent.getExtras().getString(AppConstants.YEAR));
			} else if (intent.getAction().equals(Intents.SONG_COVER)) {
				new ImageDecodeTask().execute();
			} else if (intent.getAction().equals(Intents.PLAY_STATE)) {
				if (intent.getExtras().getString(AppConstants.STATE).equals(AppConstants.PLAYING)) {
					getImageButtonById(R.id.playPauseButton).setImageResource(
							R.drawable.ic_media_pause);
					getImageButtonById(R.id.stopButton).setImageResource(
							R.drawable.ic_media_stop);
				} else if (intent.getExtras().getString(AppConstants.STATE).equals(AppConstants.PAUSED)) {
					getImageButtonById(R.id.playPauseButton).setImageResource(
							R.drawable.ic_media_play);
				} else if (intent.getExtras().getString(AppConstants.STATE).equals(AppConstants.STOPPED)) {
					getImageButtonById(R.id.playPauseButton).setImageResource(
							R.drawable.ic_media_play);
					getImageButtonById(R.id.stopButton).setImageResource(
							R.drawable.ic_media_stop_pressed);
				}
			} else if (intent.getAction().equals(Intents.MUTE_STATE)) {
				if (intent.getExtras().getString(AppConstants.STATE).equalsIgnoreCase(AppConstants.TRUE)) {
					getImageButtonById(R.id.muteButton).setImageResource(
							R.drawable.ic_media_mute_active);
				} else {
					getImageButtonById(R.id.muteButton).setImageResource(
							R.drawable.ic_media_mute_full);
				}
			} else if (intent.getAction().equals(Intents.REPEAT_STATE)) {
				if (intent.getExtras().getString(AppConstants.STATE).equalsIgnoreCase(AppConstants.ALL)) {
					getImageButtonById(R.id.repeatButton).setImageResource(
							R.drawable.ic_media_repeat);
				} else {
					getImageButtonById(R.id.repeatButton).setImageResource(
							R.drawable.ic_media_repeat_off);
				}
			} else if (intent.getAction().equals(Intents.SHUFFLE_STATE)) {
				if (intent.getExtras().getString(AppConstants.STATE).equalsIgnoreCase(AppConstants.TRUE)) {
					getImageButtonById(R.id.shuffleButton).setImageResource(
							R.drawable.ic_media_shuffle);
				} else {
					getImageButtonById(R.id.shuffleButton).setImageResource(
							R.drawable.ic_media_shuffle_off);
				}
			} else if (intent.getAction().equals(Intents.SCROBBLER_STATE)) {
				if (intent.getExtras().getString(AppConstants.STATE).equalsIgnoreCase(AppConstants.TRUE)) {
					getImageButtonById(R.id.scrobbleButton).setImageResource(
							R.drawable.ic_media_scrobble_red);
				} else {
					getImageButtonById(R.id.scrobbleButton).setImageResource(
							R.drawable.ic_media_scrobble_off);
				}
			} else if (intent.getAction().equals(Intents.LYRICS_DATA)) {
				processLyricsData();
			}
		}
	};

	private class ImageDecodeTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			if (ReplyHandler.getInstance().getCoverData() == "")
				return BitmapFactory.decodeResource(getApplicationContext()
						.getResources(), R.drawable.ic_image_no_cover);
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
		bindService(new Intent(MainActivity.this, ConnectivityHandler.class),
				mConnection, Context.BIND_AUTO_CREATE);
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

	/**
     *
     */
	private void showToastWindow(int id) {
		Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
	}

	/**
     *
     */
	private void processLyricsData() {
		if (ReplyHandler.getInstance().getSongLyrics().contains("")) {
			showToastWindow(R.string.no_lyrics_found);
			return;
		}
		LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this
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

		((TextView) lyricsPopup.getContentView().findViewById(R.id.lyricsText))
				.setText(ReplyHandler.getInstance().getSongLyrics());
		lyricsPopup.getContentView().findViewById(R.id.popup_close_button)
				.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        lyricsPopup.dismiss();

                    }
                });
		lyricsPopup.showAtLocation(findViewById(R.id.mainLinearLayout),
				Gravity.CENTER, 0, 0);
		ReplyHandler.getInstance().clearLyrics();
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
					AppConstants.TOGGLE);
		}
	};

	private OnClickListener scrobbleButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Scrobble,
					AppConstants.TOGGLE);

		}
	};

	private OnClickListener shuffleButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Shuffle,
					AppConstants.TOGGLE);
		}
	};

	private OnClickListener repeatButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.requestHandler().requestAction(PlayerAction.Repeat,
					AppConstants.TOGGLE);
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