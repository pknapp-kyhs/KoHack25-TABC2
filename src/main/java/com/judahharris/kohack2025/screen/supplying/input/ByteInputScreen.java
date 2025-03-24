package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.util.function.Consumer;

public class ByteInputScreen extends SupplyingScreen<Byte> {

    private String prompt = "Please enter a byte value: ";

    public ByteInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public ByteInputScreen(Consumer<Byte> consumer) {
        super(consumer);
    }

    public ByteInputScreen(String prompt, Consumer<Byte> consumer) {
        this(prompt);
        this.consumer = consumer;
    }

    @Override
    public Byte get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextByte();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid byte.");
            getScanner().next();
            return get();
        }
    }
}
