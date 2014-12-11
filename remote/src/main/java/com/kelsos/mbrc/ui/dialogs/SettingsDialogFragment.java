package com.kelsos.mbrc.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import roboguice.fragment.provided.RoboDialogFragment;
import roboguice.inject.InjectView;

import static com.kelsos.mbrc.util.RemoteUtils.isNullOrEmpty;

public class SettingsDialogFragment extends RoboDialogFragment {
    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 1;

	@InjectView(R.id.settings_dialog_host)
    private EditText hostEdit;

	@InjectView(R.id.settings_dialog_name)
    private EditText nameEdit;

	@InjectView(R.id.settings_dialog_port)
    private EditText portEdit;

	@InjectView(R.id.settings_dialog_http)
	private EditText httpEdit;

    private String currentName;
    private String currentAddress;
    private int currentPort;
    private int currentIndex;
	private int currentHttpPort;

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
		MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.customView(R.layout.ui_dialog_settings);
		builder.title(R.string.settings_dialog_add);
		builder.positiveText(R.string.settings_dialog_add);
		builder.negativeText(android.R.string.cancel);
		builder.callback(new MaterialDialog.Callback() {
			@Override
			public void onPositive(MaterialDialog materialDialog) {
				boolean shouldIClose = true;
				String hostname = hostEdit.getText().toString();
				String computerName = nameEdit.getText().toString();

				if (hostname.length() == 0 || computerName.length() == 0) {
					shouldIClose = false;
				}

				String portText = portEdit.getText().toString();
				String httpText = httpEdit.getText().toString();

				int portNum = isNullOrEmpty(portText) ? 0 : Integer.parseInt(portText);
				int httpNum = isNullOrEmpty(httpText) ? 0 : Integer.parseInt(httpText);

				if (isValid(portNum) && isValid(httpNum) && shouldIClose) {
					ConnectionSettings settings = new ConnectionSettings(hostname, computerName, portNum, currentIndex, httpNum);
					mListener.onDialogPositiveClick(SettingsDialogFragment.this, settings);
					materialDialog.dismiss();
				}
			}

			@Override
			public void onNegative(MaterialDialog materialDialog) {
				materialDialog.cancel();
			}
		});

        return builder.build();
    }

    @Override public void onStart() {
        super.onStart();

        if (nameEdit != null && !nameEdit.getText().toString().equals("")) {
            nameEdit.setText(currentName);
            hostEdit.setText(currentAddress);
			httpEdit.setText(currentHttpPort);
            if (currentPort > 0) {
                portEdit.setText(Integer.toString(currentPort));
            }
        }
    }

    private boolean isValid(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            final MaterialDialog.Builder alert = new MaterialDialog.Builder(getActivity());
            alert.title(R.string.alert_invalid_range);
            alert.content(R.string.alert_invalid_port_number);
            alert.positiveText(android.R.string.ok);
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
            currentIndex = args.getInt("index");
            currentPort = args.getInt("port");
            currentAddress = args.getString("address");
            currentName = args.getString("name");
			currentHttpPort = args.getInt("http");
        }
    }

    public interface SettingsDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, ConnectionSettings settings);
    }
}
