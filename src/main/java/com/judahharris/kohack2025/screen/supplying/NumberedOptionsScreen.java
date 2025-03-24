package com.judahharris.kohack2025.screen.supplying;

import com.judahharris.kohack2025.screen.Displayable;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
@RequiredArgsConstructor
public class NumberedOptionsScreen<T> extends SupplyingScreen<T> {

    private String prompt = "Please choose one of the following options:";
    private final List<T> options;

    public NumberedOptionsScreen(List<T> options, Consumer<T> consumer) {
        super(consumer);
        this.options = options;
    }

    public NumberedOptionsScreen(String prompt, List<T> options, Consumer<T> consumer) {
        super(consumer);
        this.prompt = prompt;
        this.options = options;
    }

    public T get() {
        getPrintStream().println(prompt);
        for (int i = 0; i < options.size(); i++) {
            T option = options.get(i);
            getPrintStream().println((i + 1) + " - " + ((option instanceof Displayable displayable) ? displayable.display() : option));
        }
        try {
            int input = getScanner().nextInt();
            if (input > 0 && input <= options.size()) {
                return options.get(input - 1);
            } else {
                getPrintStream().println("Invalid input");
                return get();
            }
        } catch (Exception e) {
            getPrintStream().println("Invalid input");
            getScanner().nextLine();
            return get();
        }

    }
}
