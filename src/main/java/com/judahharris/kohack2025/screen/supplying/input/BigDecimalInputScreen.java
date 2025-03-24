package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class BigDecimalInputScreen extends SupplyingScreen<BigDecimal> {

    private String prompt = "Please enter a BigDecimal value: ";

    public BigDecimalInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public BigDecimalInputScreen(Consumer<BigDecimal> consumer) {
        super(consumer);
    }

    public BigDecimalInputScreen(String prompt, Consumer<BigDecimal> consumer) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public BigDecimal get() {
        getPrintStream().println(prompt);
        try {
            return getScanner().nextBigDecimal();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a valid BigDecimal.");
            getScanner().next();
            return get();
        }
    }
}
