package com.judahharris.kohack2025.screen.supplying;

import com.judahharris.kohack2025.screen.Screen;
import com.judahharris.kohack2025.screen.ScreenContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

@AllArgsConstructor
@NoArgsConstructor
public abstract class SupplyingScreen<T> extends Screen implements Supplier<T> {

    protected Consumer<T> consumer;

    @Override
    public void display() {
        if (consumer != null) {
            consumer.accept(get());
        }
    }

    public SupplyingScreen<T> setScreenContext(ScreenContext screenContext) {
        super.setScreenContext(screenContext);
        return this;
    }
}
