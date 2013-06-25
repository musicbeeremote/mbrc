package com.kelsos.mbrc.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.NoSettingsAvailable;
import com.kelsos.mbrc.ui.fragments.DrawerFragment;
import com.kelsos.mbrc.ui.fragments.MainFragment;
import com.kelsos.mbrc.ui.fragments.NowPlayingFragment;
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import net.simonvt.menudrawer.MenuDrawer;

import java.lang.reflect.Field;

public class MainFragmentActivity extends RoboSherlockFragmentActivity {

    @Inject Bus bus;
    private MenuDrawer mDrawer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.ui_main_container);

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

            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = MenuDrawer.attach(this);
        mDrawer.setContentView(R.layout.ui_main_container);
        mDrawer.setMenuView(R.layout.ui_drawer_placeholder);
        if (isTablet()) {
            mDrawer.setMenuSize(350);
        }


        DrawerFragment smFragment = new DrawerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.sliding_menu_placeholder,smFragment).commit();
	}

    public void closeDrawer() {
        if (mDrawer!=null) {
            mDrawer.closeMenu();
        }
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
	public void onDestroy(){
		super.onDestroy();
	}

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0){
                    onBackPressed();
                    if(fm.getBackStackEntryCount()<=0){
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                }
                return true;
			default:
				return false;
		}
	}

    @Subscribe public void ShowSetupDialog(NoSettingsAvailable noSettings) {
        DialogFragment dialog = new SetupDialogFragment();
        dialog.show(getSupportFragmentManager(),"SetupDialogFragment");
    }
}
