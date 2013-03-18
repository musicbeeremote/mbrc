package com.kelsos.mbrc.commands;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.ui.SetupDialogFragment;

public class CreateSetupDialog implements ICommand {

    @Inject
    ActiveFragmentProvider afp;
    @Override
    public void execute(IEvent e) {
        DialogFragment dialog = new SetupDialogFragment();
        dialog.show(((FragmentActivity) afp.getActivity()).getSupportFragmentManager(),"SetupDialogFragment");
    }
}
