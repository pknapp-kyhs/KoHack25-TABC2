package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class FloatInputScreen extends SupplyingScreen<Float> {

    private String prompt = "Please enter a float value: ";

    public FloatInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public FloatInputScreen(Consumer<Float> consumer) {
        super(consumer);
    }

    public FloatInputScreen(Consumer<Float> consumer, String prompt) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public Float get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextFloat();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid float.");
            getScanner().next();
            return get();
        }
    }
}
