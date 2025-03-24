package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class IntegerInputScreen extends SupplyingScreen<Integer> {

    private String prompt = "Please enter a value: ";

    public IntegerInputScreen(Consumer<Integer> consumer) {
        super(consumer);
    }

    public IntegerInputScreen(String prompt, Consumer<Integer> consumer) {
        super(consumer);
        this.prompt = prompt;
    }

    @Override
    public Integer get() {
        return promptInt(prompt);
    }
}
