package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsDialogFragment extends DialogFragment {

  private static final int MAX_PORT = 65535;
  private static final int MIN_PORT = 1;

  @BindView(R.id.settings_dialog_host)
  EditText hostEdit;
  @BindView(R.id.settings_dialog_name)
  EditText nameEdit;
  @BindView(R.id.settings_dialog_port)
  EditText portEdit;

  private SettingsSaveListener mListener;
  private ConnectionSettings settings;
  private boolean edit;

  public static SettingsDialogFragment newInstance(ConnectionSettings settings) {
    SettingsDialogFragment fragment = new SettingsDialogFragment();
    fragment.setConnectionSettings(settings);
    fragment.edit = true;
    return fragment;
  }

  private void setConnectionSettings(ConnectionSettings settings) {
    this.settings = settings;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (SettingsSaveListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement SettingsDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.theme(Theme.DARK);
    builder.customView(R.layout.ui_dialog_settings, false);
    builder.title(edit ? R.string.settings_dialog_edit : R.string.settings_dialog_add);
    builder.positiveText(edit ? R.string.settings_dialog_save : R.string.settings_dialog_add);
    builder.negativeText(android.R.string.cancel);
    builder.onPositive((dialog, which) -> {
      boolean shouldIClose = true;
      String hostname = hostEdit.getText().toString();
      String computerName = nameEdit.getText().toString();

      if (hostname.length() == 0 || computerName.length() == 0) {
        shouldIClose = false;
      }

      String portText = portEdit.getText().toString();

      int portNum = TextUtils.isEmpty(portText) ? 0 : Integer.parseInt(portText);
      if (isValid(portNum) && shouldIClose) {
        settings.setName(computerName);
        settings.setAddress(hostname);
        settings.setPort(portNum);
        mListener.onSave(settings);
        dialog.dismiss();
      }
    });
    builder.onNegative((dialog, which) -> dialog.dismiss());

    final MaterialDialog settingsDialog = builder.build();
    final View view = settingsDialog.getCustomView();

    if (view == null) {
      return settingsDialog;
    }

    ButterKnife.bind(this, view);
    return settingsDialog;
  }

  @Override
  public void onStart() {
    super.onStart();
    nameEdit.setText(settings.getName());
    hostEdit.setText(settings.getAddress());

    if (settings.getPort() > 0) {
      portEdit.setText(String.valueOf(settings.getPort()));
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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (settings == null) {
      settings = new ConnectionSettings();
    }

  }

  public interface SettingsSaveListener {
    void onSave(ConnectionSettings settings);
  }
}
