package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class DoubleInputScreen extends SupplyingScreen<Double> {

    private String prompt = "Please enter a value: ";

    public DoubleInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public DoubleInputScreen(Consumer<Double> consumer) {
        super(consumer);
    }

    public DoubleInputScreen(String prompt, Consumer<Double> consumer) {
        this.prompt = prompt;
        super.consumer = consumer;
    }

    @Override
    public Double get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextDouble();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid double.");
            getScanner().next();
            return get();
        }
    }
}
