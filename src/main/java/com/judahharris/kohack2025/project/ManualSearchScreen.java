package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.ai.AiService;
import com.judahharris.kohack2025.screen.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ManualSearchScreen extends Screen {

    @Autowired
    private AiService aiService;

    @Override
    public void display() {
        String result = aiService.manualSearch(promptNonEmptyString("Enter a search term: "));
        getPrintStream().println(result);
        if (promptYesNoBoolean("Would you like to search again? (yes/no)")) {
            display();
        }
        screenContext.createScreenBean(WelcomeScreen.class).display();
    }
}
