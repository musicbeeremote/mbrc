package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.enums.DisplayFragment;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.DrawerEvent;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment;
import com.kelsos.mbrc.ui.dialogs.UpgradeDialogFragment;
import com.kelsos.mbrc.ui.fragments.*;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainFragmentActivity extends BaseActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mDrawerMenu;
    private DisplayFragment mDisplay;
    private boolean navChanged;
    private DialogFragment mDialog;
    private MenuItem favoriteItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.ui_main_container);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerMenu = findViewById(R.id.drawer_menu);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
                if (navChanged) {
                    navigateToView();
                }
            }

            public void onDrawerOpened(View view) {
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            return;
        }

        navChanged = false;
        mDisplay = DisplayFragment.HOME;

        MainFragment mFragment = new MainFragment();
        mFragment.setArguments(getIntent().getExtras());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mFragment, "main_fragment");
        fragmentTransaction.commit();
    }

    private void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
            mDrawerLayout.closeDrawer(mDrawerMenu);
        }
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        favoriteItem = menu.findItem(R.id.action_bar_favorite);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
                    mDrawerLayout.closeDrawer(mDrawerMenu);
                } else {
                    mDrawerLayout.openDrawer(mDrawerMenu);
                }

                return true;
            case R.id.actionbar_settings:
                startActivity(new Intent(this, AppPreferenceView.class));
                return true;
            case R.id.actionbar_help:
                Intent openHelp = new Intent(Intent.ACTION_VIEW);
                openHelp.setData(Uri.parse("http://kelsos.net/musicbeeremote/help/"));
                startActivity(openHelp);
                return true;
            case R.id.action_bar_favorite:
                final UserAction loveAction = new UserAction(Protocol.NowPlayingLfmRating, Const.TOGGLE);
                final MessageEvent userAction = new MessageEvent(ProtocolEventType.UserAction, loveAction);
                getBus().post(userAction);
                return true;
            default:
                return false;
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Subscribe public void showSetupDialog(DisplayDialog event) {
        if (mDialog != null) return;
        if (event.getDialogType() == DisplayDialog.SETUP) {
            mDialog = new SetupDialogFragment();
            mDialog.show(getSupportFragmentManager(), "SetupDialogFragment");
        } else if (event.getDialogType() == DisplayDialog.UPGRADE) {
            mDialog = new UpgradeDialogFragment();
            mDialog.show(getSupportFragmentManager(), "UpgradeDialogFragment");
        } else if (event.getDialogType() == DisplayDialog.INSTALL) {
            mDialog = new UpgradeDialogFragment();
            ((UpgradeDialogFragment)mDialog).setNewInstall(true);
            mDialog.show(getSupportFragmentManager(), "UpgradeDialogFragment");
        }

    }

    @Subscribe public void handleDrawerEvent(DrawerEvent event) {
        if (event.isCloseDrawer()) {
            closeDrawer();
        } else {
            navChanged = true;
            mDisplay = event.getNavigate();
            closeDrawer();
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        int bsCount = fragmentManager.getBackStackEntryCount();

        for (int i = 0; i < bsCount; i++) {
            int bsId = fragmentManager.getBackStackEntryAt(i).getId();
            fragmentManager.popBackStack(bsId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void navigateToView() {
        switch (mDisplay) {
            case HOME:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    onBackPressed();
                }
                break;
            case SEARCH:
                BrowseFragment slsFragment = new BrowseFragment();
                replaceFragment(slsFragment, "library_search");
                break;
            case NOW_PLAYING_LIST:
                NowPlayingFragment npFragment = new NowPlayingFragment();
                replaceFragment(npFragment, "now_playing_list");
                break;
            case LYRICS:
                LyricsFragment lFragment = new LyricsFragment();
                replaceFragment(lFragment, "lyrics");
                break;
            case PLAYLIST:
                PlaylistFragment plFragment = new PlaylistFragment();
                replaceFragment(plFragment, "playlist");
                break;
        }
        navChanged = false;
    }

    @Subscribe public void handleUserNotification(NotifyUser event) {
        if (event.isFromResource()) {
            Crouton.makeText(this, event.getResId(), Style.INFO).show();
        } else {
            Crouton.makeText(this, event.getMessage(), Style.INFO).show();
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                getBus().post(new MessageEvent(UserInputEventType.KeyVolumeUp));
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                getBus().post(new MessageEvent(UserInputEventType.KeyVolumeDown));
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Subscribe public void handleLfmStatusChange(final LfmRatingChanged event) {
        if (favoriteItem == null) return;
        switch (event.getStatus()) {
            case LOVED:
                favoriteItem.setIcon(R.drawable.ic_action_rating_favorite);
                break;
            case BANNED:
                favoriteItem.setIcon(R.drawable.ic_media_lfm_banned);
                break;
            case NORMAL:
                favoriteItem.setIcon(R.drawable.ic_action_rating_favorite_disabled);
                break;
        }
    }
}
