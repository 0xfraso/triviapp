package com.fraso.triviapp;

import java.util.ArrayList;
import java.util.Locale;

public enum Difficulty {
    ANY("any"),
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private String value;

    private Difficulty(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ArrayList<String> getStringValues() {

        ArrayList<String> values = new ArrayList<String>();

        for (Difficulty d: Difficulty.values()) {
            values.add(d.toString().toLowerCase(Locale.ROOT));
        }

        return values;
    }
}
