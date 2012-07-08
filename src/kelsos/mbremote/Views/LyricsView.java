package kelsos.mbremote.Views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import kelsos.mbremote.R;

public class LyricsView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics);
        //Controller.getInstance().onActivityStart(this);
    }

    public void updateLyricsData(String lyrics, String artist, String title)
    {
        ((TextView)findViewById(R.id.lyricsText)).setText(lyrics);
    }
}
