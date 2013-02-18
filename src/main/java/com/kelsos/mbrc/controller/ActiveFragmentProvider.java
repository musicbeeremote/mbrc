package com.kelsos.mbrc.controller;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.commands.request.RequestAllArtists;
import com.kelsos.mbrc.commands.request.RequestLibrarySearch;
import com.kelsos.mbrc.commands.visual.VisualUpdateSimpleSearch;
import com.kelsos.mbrc.configuration.LyricsViewCommandRegistration;
import com.kelsos.mbrc.configuration.MainViewCommandRegistration;
import com.kelsos.mbrc.configuration.PlaylistViewCommandRegistration;
import com.kelsos.mbrc.configuration.SlidingMenuCommandRegistration;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.fragments.*;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ActiveFragmentProvider
{
	private Controller controller;
	private List<Fragment> fragmentList;

	@Inject
	public ActiveFragmentProvider(Controller controller) {
		this.controller = controller;
		this.fragmentList = new ArrayList<Fragment>();

	}

	public void addActiveFragment(Fragment fragment)
	{
		if(this.fragmentList.contains(fragment)) return;
		this.fragmentList.add(fragment);
		registerCommands(fragment);
	}

	public void removeActiveFragment(Fragment fragment)
	{
		this.fragmentList.remove(fragment);
		unRegisterCommands(fragment);
	}

	public Fragment getActiveFragment(Class classname)
	{
		Fragment value = null;
		for(Fragment cFragment:fragmentList){
			if(classname.isInstance(cFragment)){
				value = cFragment;
			}
		}
		return value;
	}

    public Activity getActivity(){
        for(Fragment cFragment:fragmentList){
            if(cFragment!=null){
                return cFragment.getActivity();
            }
        }
        return null;
    }

	private void registerCommands(Fragment fragment)
	{
		if(fragment.getClass() == MainFragment.class)
		{
			MainViewCommandRegistration.register(controller);
		}
		else if(fragment.getClass() == LyricsFragment.class)
		{
			LyricsViewCommandRegistration.register(controller);
		}
		else if(fragment.getClass() == NowPlayingFragment.class)
		{
			PlaylistViewCommandRegistration.register(controller);
		}
        else if(fragment.getClass() == SlidingMenuFragment.class)
        {
            SlidingMenuCommandRegistration.register(controller);
        }
        else if (fragment.getClass() == SimpleLibrarySearchFragment.class){
            controller.registerCommand(UserInputEventType.USERINPUT_EVENT_LIB_REQUEST_SEARCH, RequestLibrarySearch.class);
            controller.registerCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LIBRARY_SEARCH_DATA, VisualUpdateSimpleSearch.class);
        }
	//	else if(fragment.getClass()== AppPreferenceView.class)
	//	{
	//		PreferenceViewCommandRegistration.register(controller);
	//	}
	}

	private void unRegisterCommands(Fragment fragment)
	{
		if(fragment.getClass() == MainFragment.class)
		{
			MainViewCommandRegistration.unRegister(controller);
		}
		else if(fragment.getClass() == LyricsFragment.class)
		{
			LyricsViewCommandRegistration.unRegister(controller);
		}
		else if(fragment.getClass() == NowPlayingFragment.class)
		{
			PlaylistViewCommandRegistration.unRegister(controller);
		}
        else if (fragment.getClass() == SimpleLibrarySearchFragment.class){
            controller.unRegisterCommand(UserInputEventType.USERINPUT_EVENT_LIB_REQUEST_SEARCH, RequestLibrarySearch.class);
            controller.unRegisterCommand(ProtocolHandlerEventType.PROTOCOL_HANDLER_LIBRARY_SEARCH_DATA, VisualUpdateSimpleSearch.class);
        }

//		else if(fragment.getClass()== AppPreferenceView.class)
//		{
//			PreferenceViewCommandRegistration.unRegister(controller);
//		}
	}


}
