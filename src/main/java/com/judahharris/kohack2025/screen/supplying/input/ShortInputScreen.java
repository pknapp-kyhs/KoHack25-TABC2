package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class ShortInputScreen extends SupplyingScreen<Short> {

    private String prompt = "Please enter a short value: ";

    public ShortInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public ShortInputScreen(Consumer<Short> consumer) {
        super(consumer);
    }

    public ShortInputScreen(Consumer<Short> consumer, String prompt) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public Short get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextShort();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid short.");
            getScanner().next();
            return get();
        }
    }
}
