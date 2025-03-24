package com.judahharris.kohack2025.screen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Option implements Runnable, Displayable {

    private Runnable action;
    private String displayText;

    @Override
    public String display() {
        return displayText;
    }

    @Override
    public void run() {
        action.run();
    }
}
