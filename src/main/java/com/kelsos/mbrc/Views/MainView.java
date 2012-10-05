package com.kelsos.mbrc.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.UserActionEvent;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;

import java.util.Timer;
import java.util.TimerTask;

public class MainView extends RoboSherlockActivity
{
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

	// Injects
	@Inject
	protected Bus bus;
	@Inject
	private RunningActivityAccessor accessor;

	private boolean userChangingVolume;
	private Timer progressUpdateTimer;
	private TimerTask progressUpdateTask;

	private ShareActionProvider mShareActionProvider;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		accessor.register(this);
		userChangingVolume = false;
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH));

		SetTextViewTypeface();
		RegisterListeners();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH));
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_DATA_REFRESH));
	}

	@Override
	protected void onPause()
	{
		accessor.unRegister(this);
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		accessor.unRegister(this);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		accessor.unRegister(this);
		super.onDestroy();
	}

	/**
	 * Sets the typeface of the text views in the main activity to roboto.
	 */
	private void SetTextViewTypeface()
	{		/* Marquee Hack */
		try{
		artistLabel.setSelected(true);
		titleLabel.setSelected(true);
		albumLabel.setSelected(true);
		yearLabel.setSelected(true);

		Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
		artistLabel.setTypeface(robotoLight);
		titleLabel.setTypeface(robotoLight);
		albumLabel.setTypeface(robotoLight);
		yearLabel.setTypeface(robotoLight);
		trackProgressCurrent.setTypeface(robotoRegular);
		trackDuration.setTypeface(robotoRegular);
		}
		catch (Exception ignore)
		{

		}
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		MenuItem shareItem = menu.findItem(R.id.main_menu_share);
		mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		return true;
	}

	private void setShareIntent(Intent shareIntent)
	{
		if(mShareActionProvider!=null) mShareActionProvider.setShareIntent(shareIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.main_menu_settings:
				startActivity(new Intent(this, AppPreferenceView.class));
				return true;
			case R.id.main_menu_lyrics:
				startActivity(new Intent(this, LyricsView.class));
				return true;
			case R.id.main_menu_playlist:
				startActivity(new Intent(this, PlaylistView.class));
				return true;
			case R.id.main_menu_share:
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "Now Playing: " + artistLabel.getText() + " - " + titleLabel.getText());
				setShareIntent(shareIntent);
			default:
				return false;
		}
	}


	/**
	 * Registers the listeners for the interface elements used for interaction.
	 */
	private void RegisterListeners()
	{
		try{
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
		} catch (Exception ignore)
		{

		}

	}

	/**
	 * Given a boolean state this function updates the Scrobbler button with the proper state.
	 * Also it updates the internal MainActivityState object.
	 *
	 * @param state If true it means that the scrobbler is active, false is used for inactive.
	 */
	public void updateScrobblerButtonState(boolean state)
	{
		if (state)
		{
			scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_red);
		} else
		{
			scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_off);
		}
	}

	public void resetAlbumCover()
	{
		albumCover.setImageResource(R.drawable.ic_image_no_cover);
	}

	public void updateAlbumCover(Bitmap cover)
	{
		albumCover.setImageBitmap(cover);
	}

	/**
	 * Given a boolean state value this function updates the shuffle button with the proper state.
	 * Also it updates the internal MainActivityState object.
	 *
	 * @param state True is used to represent active shuffle, false is used for inactive.
	 */
	public void updateShuffleButtonState(boolean state)
	{
		if (state)
		{
			shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
		} else
		{
			shuffleButton.setImageResource(R.drawable.ic_media_shuffle_off);
		}
	}

	public void updateRepeatButtonState(boolean state)
	{
		if (state)
		{
			repeatButton.setImageResource(R.drawable.ic_media_repeat);
		} else
		{
			repeatButton.setImageResource(R.drawable.ic_media_repeat_off);
		}
	}

	public void updateMuteButtonState(boolean state)
	{
		if (state)
		{
			muteButton.setImageResource(R.drawable.ic_media_mute_active);
		} else
		{
			muteButton.setImageResource(R.drawable.ic_media_mute_full);
		}
	}

	public void updateVolumeData(int volume)
	{
		if (!userChangingVolume)
			volumeSlider.setProgress(volume);
	}

	public void updatePlayState(PlayState playState)
	{
		switch (playState)
		{
			case Playing:
				playPauseButton.setImageResource(R.drawable.ic_media_pause);
				playPauseButton.setTag("Playing");
				stopButton.setImageResource(R.drawable.ic_media_stop);
				stopButton.setEnabled(true);
                /* Start the animation if the track is playing*/
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

	public void updateArtistText(String artist)
	{
		artistLabel.setText(artist);
	}

	public void updateTitleText(String title)
	{
		titleLabel.setText(title);
	}

	public void updateAlbumText(String album)
	{
		albumLabel.setText(album);
	}

	public void updateYearText(String year)
	{
		yearLabel.setText(year);
	}

	public void updateConnectivityStatus(ConnectionStatus status)
	{
		switch (status)
		{
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

	private void activateStoppedState()
	{
		trackProgressSlider.setProgress(0);
		trackProgressCurrent.setText("00:00");
		stopButton.setEnabled(false);
	}

	/**
	 * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
	 * current progress of playback
	 *
	 * @param current Integer represents the current playback position in milliseconds
	 * @param total   Integer represents the total track duration in milliseconds
	 */
	public void updateDurationDisplay(int current, int total)
	{
		int currentSeconds = current / 1000;
		int totalSeconds = total / 1000;

		int currentMinutes = currentSeconds / 60;
		int totalMinutes = totalSeconds / 60;

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
	private void trackProgressAnimation()
	{
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
		final int timerPeriod = 100;
		stopTrackProgressAnimation();
		if (!stopButton.isEnabled() || playPauseButton.getTag() == "Paused") return;
		progressUpdateTimer = new Timer(true);
		progressUpdateTask = new TimerTask()
		{
			@Override
			public void run()
			{
				int currentProgress = trackProgressSlider.getProgress() / 1000;
				final int currentMinutes = currentProgress / 60;
				final int currentSeconds = currentProgress % 60;
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						trackProgressSlider.setProgress(trackProgressSlider.getProgress() + timerPeriod);
						trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
					}
				});

			}
		};
		progressUpdateTimer.schedule(progressUpdateTask, 0, timerPeriod);
	}

	/**
	 * If the track progress animation is running the the function stops it.
	 */
	private void stopTrackProgressAnimation()
	{
		if (progressUpdateTask != null)
		{
			progressUpdateTask.cancel();
			progressUpdateTask = null;
			progressUpdateTimer.cancel();
			progressUpdateTimer.purge();
			progressUpdateTimer = null;
		}

	}

	private OnClickListener playButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAY_PAUSE));
		}
	};

	private OnClickListener previousButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_PREVIOUS));
		}
	};

	private OnClickListener nextButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NEXT));
		}
	};

	private OnClickListener stopButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_STOP));
		}
	};

	private OnClickListener muteButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_MUTE));
		}
	};

	private OnClickListener scrobbleButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_LAST_FM));
		}
	};

	private OnClickListener shuffleButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_SHUFFLE));
		}
	};

	private OnClickListener repeatButtonListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_REPEAT));
		}
	};
	private OnClickListener connectivityIndicatorListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE));
		}
	};

	private View.OnLongClickListener connectivityIndicatorLongClickListener = new View.OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View view)
		{
			bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_CONNECTION_RESET));
			return false;
		}
	};

	private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener()
	{

		public void onStopTrackingTouch(SeekBar seekBar)
		{
			userChangingVolume = false;

		}

		public void onStartTrackingTouch(SeekBar seekBar)
		{
			userChangingVolume = true;

		}

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (fromUser)
			{
				bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME, String.valueOf(seekBar.getProgress())));
			}
		}
	};

	private OnSeekBarChangeListener durationSeekBarChangeListener = new OnSeekBarChangeListener()
	{
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (fromUser)
			{
				bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_PLAYBACK_POSITION, String.valueOf(progress)));
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar)
		{
		}

		public void onStopTrackingTouch(SeekBar seekBar)
		{
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (volumeSlider.getProgress() <= 90)
				{
					int mod = volumeSlider.getProgress()%10;
					if(mod==0) {
						volumeSlider.setProgress(volumeSlider.getProgress()+10);
					}
					else if (mod<5) {
						volumeSlider.setProgress(volumeSlider.getProgress()+(10-mod));
					}
					else {
						volumeSlider.setProgress(volumeSlider.getProgress()+(20-mod));
					}
					bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME, String.valueOf(volumeSlider.getProgress())));
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (volumeSlider.getProgress() >= 10)
				{
					int mod = volumeSlider.getProgress()%10;
					if(mod==0) {
						volumeSlider.setProgress(volumeSlider.getProgress()-10);
					}
					else if (mod<5) {
						volumeSlider.setProgress(volumeSlider.getProgress()-(10+mod));
					}
					else {
						volumeSlider.setProgress(volumeSlider.getProgress()-mod);
					}
					bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_VOLUME, String.valueOf(volumeSlider.getProgress())));
				}
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_VOLUME_UP:
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				return true;
			default:
				return super.onKeyUp(keyCode, event);

		}
	}
}