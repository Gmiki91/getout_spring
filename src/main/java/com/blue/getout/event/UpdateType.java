package com.blue.getout.event;

public enum UpdateType {
    DELETED("deleted"),
    MODIFIED("modified");

    public final String label;
    private UpdateType(String label) {
        this.label=label;
    }
}

