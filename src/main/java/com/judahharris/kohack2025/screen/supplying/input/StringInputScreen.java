package com.judahharris.kohack2025.screen.supplying.input;

import com.judahharris.kohack2025.screen.supplying.SupplyingScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Setter
@Getter
@AllArgsConstructor
public class StringInputScreen extends SupplyingScreen<String> {

    private String prompt = "Please enter a value: ";
    private boolean allowBlank = false;

    public StringInputScreen(Consumer<String> consumer) {
        super(consumer);
    }

    public StringInputScreen(String prompt, Consumer<String> consumer) {
        super(consumer);
        this.prompt = prompt;
    }

    public StringInputScreen(String prompt) {
        this.prompt = prompt;
    }

    public StringInputScreen(Consumer<String> consumer, boolean allowBlank) {
        super(consumer);
        this.allowBlank = allowBlank;
    }

    public StringInputScreen(String prompt, Consumer<String> consumer, boolean allowBlank) {
        super(consumer);
        this.prompt = prompt;
        this.allowBlank = allowBlank;
    }

    @Override
    public String get() {
        if (allowBlank) {
            return promptString(prompt);
        }
        return promptNonEmptyString(prompt);
    }
}
