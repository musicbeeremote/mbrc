package kelsos.mbremote.Views;

import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.R;
import kelsos.mbremote.controller.RunningActivityAccessor;
import kelsos.mbremote.enums.UserInputEventType;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.lyrics)
public class LyricsView extends RoboSherlockActivity
{
	@InjectView(R.id.lyricsText) TextView lyricsText;
	@InjectView(R.id.lyricsLabel) TextView trackInfo;
	@Inject
	Bus bus;
	@Inject
	RunningActivityAccessor accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.Lyrics));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.string_lyrics);

    }

	@Override
	protected void onStart()
	{
		lyricsText.setText("");
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		lyricsText.setText("");
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		accessor.unRegister(this);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    public void updateLyricsData(String lyrics, String artist, String title)
    {
        lyricsText.setText(lyrics);
		trackInfo.setText(artist + " - " + title);
    }
}
