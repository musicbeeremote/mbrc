package com.kelsos.mbrc.events.actions;

public class ButtonPressedEvent {
    private Button type;

    public ButtonPressedEvent(Button type) {
        this.type = type;
    }

    public enum Button {
        NEXT,
        PLAYPAUSE,
        PREVIOUS,
        STOP,
        SHUFFLE,
        REPEAT
    }

    public Button getType(){
        return type;
    }
}
