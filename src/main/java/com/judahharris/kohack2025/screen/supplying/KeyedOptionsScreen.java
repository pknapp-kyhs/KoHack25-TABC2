package com.judahharris.kohack2025.screen.supplying;

import com.judahharris.kohack2025.screen.Displayable;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
public class KeyedOptionsScreen<T> extends SupplyingScreen<T> {

    private String prompt = "Please choose one of the following options:";
    private final Map<String, T> options;
    private Function<Map<String, T>, String> displayFunction;

    public static <T> String getDisplayWithValuesFunction(Map<String, T> options) {
        return options.entrySet().stream().map(entry -> entry.getKey() + " - " + ((entry.getValue() instanceof Displayable displayable) ? displayable.display() : entry.getValue())).collect(Collectors.joining("\n"));
    }

    public static <T> String getDisplayWithoutValuesFunction(Map<String, T> options) {
        return options.keySet().stream().collect(Collectors.joining("\n"));
    }

    public KeyedOptionsScreen(Map<String, T> options, Function<Map<String, T>, String> displayFunction) {
        super();
        this.options = options;
        this.displayFunction = displayFunction;
    }

    public KeyedOptionsScreen(String prompt, Map<String, T> options) {
        super();
        this.prompt = prompt;
        this.options = options;
    }

    public KeyedOptionsScreen(Map<String, T> options, Consumer<T> consumer) {
        super(consumer);
        this.options = options;
    }

    public KeyedOptionsScreen(String prompt, Map<String, T> options, Consumer<T> consumer) {
        super(consumer);
        this.prompt = prompt;
        this.options = options;
    }


    public KeyedOptionsScreen(Map<String, T> options, Consumer<T> consumer, Function<Map<String, T>, String> displayFunction) {
        super(consumer);
        this.options = options;
        this.displayFunction = displayFunction;
    }

    public KeyedOptionsScreen(String prompt, Map<String, T> options, Consumer<T> consumer, Function<Map<String, T>, String> displayFunction) {
        super(consumer);
        this.prompt = prompt;
        this.options = options;
        this.displayFunction = displayFunction;
    }

    public T get() {
        getPrintStream().println(prompt);
        getPrintStream().println(displayFunction == null ? getDisplayWithValuesFunction(options) : displayFunction.apply(options));
        String input = promptNonEmptyString();
        return options.computeIfAbsent(input, key -> {
            getPrintStream().println("Invalid input: " + input);
            return get();
        });
    }
}
