package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.model.User;
import com.judahharris.kohack2025.screen.Option;
import com.judahharris.kohack2025.screen.ScreenContext;
import com.judahharris.kohack2025.screen.TextDisplayScreen;
import com.judahharris.kohack2025.screen.supplying.NumberedOptionsScreen;
import com.judahharris.kohack2025.screen.supplying.input.BooleanInputScreen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Scope("prototype")
public class WelcomeScreen extends NumberedOptionsScreen<Option> {

    private static String ABOUT_TEXT = """
            BinaBot is a bot that can help you learn about Torah and Judaism. Instead of giving you the answer,
            BinaBot is directed to help you learn the answer by yourself and assist you by providing sources 
            that you can learn about on your own.
            Log in or Sign up to get started!
            """;

    @Autowired
    public WelcomeScreen(ScreenContext screenContext) {
        super(
                screenContext.getUser() == null
                    ? "Welcome to BinaBot! We're excited to help you find and answer to your question!"
                    : "Hi, " + screenContext.getUsername() + "!, We're ready to answer your question!",
                screenContext.getUser() == null
                    ? List.of(
                        new Option(() -> screenContext.createScreenBean(LoginScreen.class).display(), "Log in"),
                        new Option(() -> screenContext.createScreenBean(SignupScreen.class).display(), "Sign up"),
                        new Option(() -> {
                            screenContext.createScreenBean(TextDisplayScreen.class, ABOUT_TEXT).display();
                            screenContext.createScreenBean(WelcomeScreen.class).display();
                        }, "What is BinaBot?"),
                        new Option(() -> {}, "Quit")
                    )
                    : List.of(
                        new Option(() -> {
                            screenContext.setUser(null);
                            screenContext.createScreenBean(WelcomeScreen.class).display();
                        }, "Log out"),
                        new Option(() -> screenContext.createScreenBean(QuestionScreen.class).display(), "Ask a question"),
                        new Option(() -> screenContext.createScreenBean(ViewQuestionsScreen.class).display(), "View your previous questions"),
                        new Option(() -> screenContext.createScreenBean(ManualSearchScreen.class).display(), "Manual search"),
                        new Option(() -> screenContext.createScreenBean(ContextEditScreen.class).display(), "View/edit your context about yourself"),
                        new Option(() -> {}, "Quit")
                ),
                Option::run
        );
    }
}
