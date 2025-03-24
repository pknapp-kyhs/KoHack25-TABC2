package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.math.BigInteger;
import java.util.function.Consumer;

public class BigIntegerInputScreen extends SupplyingScreen<BigInteger> {

    private String prompt = "Please enter a BigInteger value: ";

    public BigIntegerInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public BigIntegerInputScreen(Consumer<BigInteger> consumer) {
        super(consumer);
    }

    public BigIntegerInputScreen(String prompt, Consumer<BigInteger> consumer) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public BigInteger get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextBigInteger();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid BigInteger.");
            getScanner().next();
            return get();
        }
    }
}
