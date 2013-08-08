package com.kelsos.mbrc.events.ui;

public class DisplayDialog {
    public static final int NONE = 0;
    public static final int SETUP = 1;
    public static final int UPGRADE = 2;
    public static final int INSTALL = 3;

    private int dialogType;

    public DisplayDialog(int dialogType) {
        this.dialogType = dialogType;
    }

    public int getDialogType() {
        return dialogType;
    }
}
