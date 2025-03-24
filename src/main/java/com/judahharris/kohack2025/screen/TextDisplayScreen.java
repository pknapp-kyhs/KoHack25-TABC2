package com.judahharris.kohack2025.screen;

public class TextDisplayScreen extends Screen {

    private String text;

    public TextDisplayScreen(String text) {
        this.text = text;
    }

    @Override
    public void display() {
        getPrintStream().println(text);
    }
}
