package com.thomastriplett.quicknotes;

/**
 * Created by Thomas on 1/28/2018.
 */

public class Note {

    private String lastUpdate;
    private String userText;

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String toString() {
        return lastUpdate + ": " + userText;
    }
}
