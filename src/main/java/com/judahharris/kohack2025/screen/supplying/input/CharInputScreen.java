package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class CharInputScreen extends SupplyingScreen<Character> {

    private String prompt = "Please enter a character: ";

    public CharInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public CharInputScreen(Consumer<Character> consumer) {
        super(consumer);
    }

    public CharInputScreen(String prompt, Consumer<Character> consumer) {
        this.prompt = prompt;
        super.consumer = consumer;
    }

    @Override
    public Character get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().next().charAt(0);
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid character.");
            getScanner().next();
            return get();
        }
    }
}
