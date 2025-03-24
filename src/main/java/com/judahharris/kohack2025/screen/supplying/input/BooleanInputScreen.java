package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class BooleanInputScreen extends SupplyingScreen<Boolean> {

    private String prompt = "Please enter true or false: ";

    public BooleanInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public BooleanInputScreen(Consumer<Boolean> consumer) {
        super(consumer);
    }

    public BooleanInputScreen(Consumer<Boolean> consumer, String prompt) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public Boolean get() {
        return promptBoolean(prompt);
    }
}
