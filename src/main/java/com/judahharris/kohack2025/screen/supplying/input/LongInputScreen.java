package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class LongInputScreen extends SupplyingScreen<Long> {

    private String prompt = "Please enter a long value: ";

    public LongInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public LongInputScreen(Consumer<Long> consumer) {
        super(consumer);
    }

    public LongInputScreen(Consumer<Long> consumer, String prompt) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public Long get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextLong();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid long.");
            getScanner().next();
            return get();
        }
    }
}
