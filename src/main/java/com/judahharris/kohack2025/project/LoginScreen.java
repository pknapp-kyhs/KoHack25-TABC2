package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.auth.AuthService;
import com.judahharris.kohack2025.auth.UserRepository;
import com.judahharris.kohack2025.screen.Screen;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Scope("prototype")
public class LoginScreen extends Screen {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void display() {
        String username = promptNonEmptyString("Please enter your username (enter \"back\" to go back): ");
        if (!username.equalsIgnoreCase("back")) {
            if (authService.doesUserExist(username)) {
                String password = promptPassword("Please enter your password: ");
                getPrintStream().println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nLogging in..."); // Clear screen, eventually replace prompt password with hashing, but that only would work with console which isn't supported in IDEs
                if (authService.authenticate(username, password)) {
                    this.screenContext.setUser(userRepository.findByUsername(username).get());
                    getPrintStream().println("Welcome, " + screenContext.getUsername() + "!");
                } else {
                    getPrintStream().println("Invalid username or password.");
                }
            } else {
                if (promptYesNoBoolean("User does not exist. Would you like to register? (yes/no)")) {
                    screenContext.createScreenBean(SignupScreen.class).display();
                }
            }

        }
        screenContext.createScreenBean(WelcomeScreen.class).display();
    }
}
