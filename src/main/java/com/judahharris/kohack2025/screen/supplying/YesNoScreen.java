package com.judahharris.kohack2025.screen.supplying;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class YesNoScreen<T> extends KeyedOptionsScreen<T> {

    public YesNoScreen(String prompt, T optionYes, T optionNo) {
        super(prompt, createMap("yes", optionYes, "no", optionNo), options -> "(yes/no)");
    }

    public YesNoScreen(String prompt, T optionYes, T optionNo, Consumer<T> consumer) {
        super(prompt, new LinkedHashMap<>(Map.of("yes", optionYes, "no", optionNo)), consumer, options -> "(yes/no)");
    }

    private static <K, V> LinkedHashMap<K, V> createMap(K key1, V value1, K key2, V value2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }
}
