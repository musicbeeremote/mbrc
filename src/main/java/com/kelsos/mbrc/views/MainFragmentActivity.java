package com.kelsos.mbrc.views;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.fragments.LyricsFragment;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.fragments.SlidingMenuFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import java.lang.reflect.Field;

public class MainFragmentActivity extends SlidingFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.main_layout);

        if (findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState!=null){
                return;
            }
            MainFragment mFragment = new MainFragment();
            mFragment.setArguments(getIntent().getExtras());

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, mFragment);

            if(isTablet()){
                NowPlayingFragment npFragment = new NowPlayingFragment();
                fragmentTransaction.add(R.id.fragment_container_extra, npFragment);
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setBehindContentView(R.layout.sliding_menu_frame);

        SlidingMenuFragment smFragment = new SlidingMenuFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.sliding_menu_placeholder,smFragment).commit();

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setShadowDrawable(R.drawable.ic_drop_shadow_2);
        slidingMenu.setShadowWidth(60);
        slidingMenu.setBehindOffset(150);
        slidingMenu.setBehindScrollScale(0.25f);
        slidingMenu.setFadeDegree(0.25f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        setSlidingActionBarEnabled(true);
	}

    public boolean isTablet(){
        boolean value = false;
        //http://stackoverflow.com/questions/7251131/how-to-determine-the-target-device-programmatically-in-android

        if (android.os.Build.VERSION.SDK_INT >= 13) { // Honeycomb 3.2
            Configuration con = getResources().getConfiguration();
            Field fSmallestScreenWidthDp = null;
            try {
                fSmallestScreenWidthDp = con.getClass().getDeclaredField("smallestScreenWidthDp");
                if(fSmallestScreenWidthDp.getInt(con) >= 600){
                    value = true;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return value;
    }

	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>1){
                    onBackPressed();
                    if(fm.getBackStackEntryCount()<=1){
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                }
                return true;
			default:
				return false;
		}
	}
}
