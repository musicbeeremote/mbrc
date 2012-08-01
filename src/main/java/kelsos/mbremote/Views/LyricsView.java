package kelsos.mbremote.Views;

import android.os.Bundle;
import android.widget.TextView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import kelsos.mbremote.R;
import roboguice.inject.ContentView;

@ContentView(R.layout.lyrics)
public class LyricsView extends RoboSherlockActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateLyricsData(String lyrics, String artist, String title)
    {
        ((TextView)findViewById(R.id.lyricsText)).setText(lyrics);
    }
}
