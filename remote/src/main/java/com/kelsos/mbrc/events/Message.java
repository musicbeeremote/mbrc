package com.kelsos.mbrc.events;

import com.kelsos.mbrc.interfaces.IEvent;

public class Message implements IEvent {
    private String type;
    private String message;

    public Message() {
        this.type = "";
        this.message = "";
    }

    public Message(String type) {
        this.type = type;
        this.message = "";
    }

    public Message(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

	@Override
	public String getMessage() {
		return message;
	}
}
