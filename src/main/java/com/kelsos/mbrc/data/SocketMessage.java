package com.kelsos.mbrc.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"dataString"})
public class SocketMessage {

    private String context;
    private String type;
    private Object data;


    public SocketMessage(String context, String type, Object data) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    public SocketMessage(){}

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDataString() {
        String result = "";
        if(data.getClass() == String.class){
            result = (String)data;
        }
        return result;
    }

}
