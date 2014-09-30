package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.*;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.kelsos.mbrc.ui.fragments.profile.AlbumTracksFragment;
import com.kelsos.mbrc.ui.fragments.profile.ArtistAlbumsFragment;
import com.kelsos.mbrc.ui.fragments.profile.GenreArtistsFragment;

public class Profile extends BaseActivity {

    public static final String GENRE = "genre";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Intent intent = getIntent();

        if (savedInstanceState == null) {
             Fragment fragment;

            String type = intent.getStringExtra("type");
            long id = intent.getLongExtra("id", 0);
            switch (type) {
                case GENRE:
                    fragment = GenreArtistsFragment.newInstance(id);
                    break;
                case ARTIST:
                    fragment = ArtistAlbumsFragment.newInstance(id);
                    break;
                case ALBUM:
                    fragment = AlbumTracksFragment.newInstance(id);
                    break;
                default:
                    fragment = new PlaceholderFragment();
                    break;
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        getSupportActionBar().setTitle(intent.getStringExtra("name"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends BaseFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_profile, container, false);
        }
    }

}
