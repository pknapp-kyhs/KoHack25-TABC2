package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.auth.UserRepository;
import com.judahharris.kohack2025.model.User;
import com.judahharris.kohack2025.screen.Screen;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ContextEditScreen extends Screen {

    private final UserRepository userRepository;

    public ContextEditScreen(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void display() {
        User user = getScreenContext().getUser();
        if (user == null) {
            getPrintStream().println("You must be logged in to edit your context.");
        } else {
            getPrintStream().println("In order to assist you better, we need to know a little bit about your background so we know what sources to recommend to you.");
            String existingContext = user.getContext();
            if (existingContext == null) {
                getPrintStream().println("You don't have any context set.");
                getPrintStream().println("Would you like to set your context now? (yes/no)");
            } else {
                getPrintStream().println("Here is your current context: ");
                getPrintStream().println(user.getContext());
                getPrintStream().println("Would you like to edit your context? (yes/no)");
            }
            if (promptYesNoBoolean()) {
                getPrintStream().println("We will ask you some questions about your Jewish background. If you are uncomfortable answering a question, feel free to leave it blank, or provide a small explanation.");
                StringBuilder background = new StringBuilder();
                background.append("Denomination: ").append(promptNonEmptyString("What is your Jewish denomination? (Orthodox, Conservative, Reform, etc.)"));
                background.append("How many visits to synagogue in a week: ").append(promptNonEmptyString("Around how many times do you visit a synagogue in a week?"));
                background.append("Preferred pronunciation (Ashkenazi (Shabbos) or Sephardic (Shabbat): ").append(promptNonEmptyString("What is your most comfortable pronunciation of Hebrew? Ashkenazi (Shabbos) or Sephardi (Shabbat)"));
                background.append("Preferred pronunciation (Hannukah or Chanukah): ").append(promptNonEmptyString("What is your preferred pronunciation of the name of this holiday? Hannukah or Chanukah"));
                background.append("Additional user comments: ").append(promptNonEmptyString("Do you have any additional comments you would like to share with the bot to enhance your experience?"));
                user.setContext(background.toString());
                userRepository.save(user);
            }
        }
        screenContext.createScreenBean(WelcomeScreen.class).display();
    }
}
