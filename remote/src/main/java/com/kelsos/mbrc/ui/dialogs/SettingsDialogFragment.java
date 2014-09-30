package com.kelsos.mbrc.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;

public class SettingsDialogFragment extends DialogFragment {
    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 1;
    private EditText host;
    private EditText name;
    private EditText port;

    private String cname;
    private String caddress;
    private int cport;
    private int cindex;

    private SettingsDialogListener mListener;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");
        }
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.ui_dialog_settings, null);
        if (view != null) {
            ((TextView) view.findViewById(R.id.dialog_title)).setText(getString(R.string.settings_dialog_add));
        }
        builder.setView(view)
                .setTitle(null)
                .setPositiveButton(R.string.settings_dialog_add,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .setNegativeButton(R.string.dialog_application_setup_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SettingsDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            name = (EditText) dialog.findViewById(R.id.settings_dialog_name);
            host = (EditText) dialog.findViewById(R.id.settings_dialog_host);
            port = (EditText) dialog.findViewById(R.id.settings_dialog_port);
        }
        if (dialog != null) {
            Button confirm = dialog.getButton(Dialog.BUTTON_POSITIVE);
            if (confirm != null) {
                confirm.setOnClickListener(new Button.OnClickListener() {
                    @Override public void onClick(View view) {
                        boolean shouldIClose = true;
                        String hostname = host.getText().toString();
                        String computerName = name.getText().toString();

                        if (hostname.length() == 0 || computerName.length() == 0) {
                            shouldIClose = false;
                        }

                        String portText = port.getText().toString();

                        int portNum = portText.equals("") ? 0 : Integer.parseInt(portText);

                        if (validatePortNumber(portNum) && shouldIClose) {
                            ConnectionSettings settings = new ConnectionSettings(hostname, computerName, portNum, cindex);
                            mListener.onDialogPositiveClick(SettingsDialogFragment.this, settings);
                            dismiss();
                        }
                    }
                });
            }
        }

        if (name != null && !name.getText().toString().equals("")) {
            name.setText(cname);
            host.setText(caddress);
            if (cport > 0) {
                port.setText(Integer.toString(cport));
            }
        }
    }

    private boolean validatePortNumber(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.alert_invalid_range);
            alert.setMessage(R.string.alert_invalid_port_number);
            alert.setPositiveButton(android.R.string.ok, null);
            alert.show();
            return false;
        } else {
            return true;
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            cindex = args.getInt("index");
            cport = args.getInt("port");
            caddress = args.getString("address");
            cname = args.getString("name");
        }
    }

    public interface SettingsDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, ConnectionSettings settings);
    }
}
