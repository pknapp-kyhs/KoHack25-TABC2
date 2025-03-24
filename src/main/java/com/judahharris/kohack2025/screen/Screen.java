package com.judahharris.kohack2025.screen;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Getter
@Setter
@NoArgsConstructor
@Component
@Accessors(chain = true)
@Scope("prototype")
public abstract class Screen implements Runnable {

    @Autowired
    protected ScreenContext screenContext;

    public InputStream getIn() {
        return screenContext.getIn();
    }

    public OutputStream getOut() {
        return screenContext.getOut();
    }

    public Scanner getScanner() {
        return screenContext.getScanner();
    }

    public PrintStream getPrintStream() {
        return screenContext.getPrintStream();
    }

    @Autowired
    public Screen(ScreenContext screenContext) {
        this.screenContext = screenContext;
    }

    protected String promptNonEmptyString() {
        String input = getScanner().nextLine();
        while (input.isEmpty()) {
            input = getScanner().nextLine();
        }
        return input.trim();
    }

    protected String promptNonEmptyString(String prompt) {
        getPrintStream().println(prompt);
        return promptNonEmptyString();
    }

    protected String promptString(String prompt) {
        getPrintStream().println(prompt);
        return promptString();
    }

    protected String promptString() {
        String input = getScanner().nextLine();
        while (!input.isEmpty()) {
            input = getScanner().nextLine();
        }
        return input;
    }

    protected int promptInt() {
        getPrintStream().println("Please enter a number.");
        try {
            return getScanner().nextInt();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a number.");
            getScanner().next();
            return promptInt();
        }
    }

    protected int promptInt(String prompt) {
        getPrintStream().println(prompt);
        return promptInt();
    }

    protected double promptDouble() {
        getPrintStream().println("Please enter a number.");
        try {
            return getScanner().nextDouble();
        } catch (Exception e) {
            getPrintStream().println("Invalid input. Please enter a number.");
            getScanner().next();
            return promptDouble();
        }
    }

    protected double promptDouble(String prompt) {
        getPrintStream().println(prompt);
        return promptDouble();
    }

    protected boolean promptBoolean() {
        getPrintStream().println("Please enter true or false.");
        String input = getScanner().nextLine();
        while (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false")) {
            getPrintStream().println("Invalid input. Please enter true or false.");
            input = getScanner().nextLine();
        }
        return Boolean.parseBoolean(input);
    }

    protected boolean promptBoolean(String prompt) {
        getPrintStream().println(prompt);
        return promptBoolean();
    }

    protected boolean promptYesNoBoolean() {
        String input = getScanner().nextLine();
        while (input.isEmpty()) {
            input = getScanner().nextLine();
        }
        while (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no")) {
            getPrintStream().println("Invalid input. Please enter yes or no.");
            input = getScanner().nextLine();
        }
        return input.equalsIgnoreCase("yes");
    }

    protected boolean promptYesNoBoolean(String prompt) {
        getPrintStream().println(prompt);
        return promptYesNoBoolean();
    }

    protected boolean promptSpecifiedOrNotBoolean(String specifiedValue, boolean caseSensitive) {
        String input = getScanner().nextLine();
        if (input.isEmpty()) {
            return false;
        }
        if (caseSensitive) {
            return input.trim().equals(specifiedValue);
        } else {
            return input.trim().equalsIgnoreCase(specifiedValue);
        }
    }

    protected boolean promptSpecifiedOrNotBoolean(String specifiedValue) {
        return promptSpecifiedOrNotBoolean(specifiedValue, false);
    }

    protected boolean promptSpecifiedOrNotBoolean(String prompt, String specifiedValue, boolean caseSensitive) {
        getPrintStream().println(prompt);
        return promptSpecifiedOrNotBoolean(specifiedValue, caseSensitive);
    }

    protected boolean promptSpecifiedOrNotBoolean(String prompt, String specifiedValue) {
        return promptSpecifiedOrNotBoolean(prompt, specifiedValue, false);
    }

    // TODO: Implement password masking
    protected String promptPassword() {
        return promptNonEmptyString();
    }

    protected String promptPassword(String prompt) {
        getPrintStream().println(prompt);
        return promptPassword();
    }

    // TODO: Implement the rest as needed and make the screens use them

    public abstract void display();

    public void run() {
        display();
    }
}
