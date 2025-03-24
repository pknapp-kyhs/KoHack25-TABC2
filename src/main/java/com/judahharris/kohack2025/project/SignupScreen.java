package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.auth.AuthService;
import com.judahharris.kohack2025.model.User;
import com.judahharris.kohack2025.screen.Option;
import com.judahharris.kohack2025.screen.Screen;
import com.judahharris.kohack2025.screen.supplying.NumberedOptionsScreen;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Scope("prototype")
public class SignupScreen extends Screen {

    private final AuthService authService;

    @Override
    public void display() {
        String username = promptNonEmptyString("Please choose a username (enter \"back\" to go back):");
        if (username.equalsIgnoreCase("back")) {
            screenContext.createScreenBean(WelcomeScreen.class).display();
            return;
        }
        if (authService.doesUserExist(username)) {
            new NumberedOptionsScreen<>("User already exists. Choose an option: ", List.of(
                    new Option(() -> screenContext.createScreenBean(LoginScreen.class).display(), "Log in"),
                    new Option(SignupScreen.this::display, "Try a new username")
            ), Option::run).setScreenContext(screenContext).display();
        } else {
            String password = promptPassword("Please enter your password (enter \"back\" to go back): ");
            if (password.equalsIgnoreCase("back")) {
                screenContext.createScreenBean(WelcomeScreen.class).display();
                return;
            }
            getPrintStream().println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nSigning up...");
            User user = authService.registerUser(username, password);
            this.screenContext.setUser(user);
            getPrintStream().println("Registered successfully! You are now signed in. Welcome, " + screenContext.getUsername() + "!");
            screenContext.createScreenBean(ContextEditScreen.class).display();
        }
    }
}
