package com.judahharris.kohack2025.screen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DisplayableWrapper implements Displayable {

    private Object object;
    private String displayString;

    @Override
    public String display() {
        return displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
