package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.model.User;
import com.judahharris.kohack2025.screen.Screen;
import com.judahharris.kohack2025.screen.ScreenContext;
import com.judahharris.kohack2025.screen.supplying.NumberedOptionsScreen;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class ViewQuestionsScreen extends Screen {

    public void display() {
        List<User.Question> questions = screenContext.getUser().getQuestions();
        if (questions == null || questions.isEmpty()) {
            screenContext.getPrintStream().println("You have no questions to view. Feel free to ask one!");
            screenContext.createScreenBean(WelcomeScreen.class).display();
        }
        List<User.Question> displayOptions = new ArrayList<>(questions);
        User.Question backQuestion = new User.Question("Back", "");
        displayOptions.addFirst(backQuestion);
        User.Question selected = new NumberedOptionsScreen<>("Select which question you'd like to view: ", displayOptions).setScreenContext(screenContext).get();
        if (selected == backQuestion) {
            screenContext.createScreenBean(WelcomeScreen.class).display();
            return;
        }
        screenContext.getPrintStream().println("Question: " + selected.getQuestion());
        screenContext.getPrintStream().println("Answer: " + selected.getAnswer());
        boolean wantsToViewAnother = promptYesNoBoolean("Would you like to view another question? (yes/no)");
        if (wantsToViewAnother) {
            display();
        } else {
            screenContext.createScreenBean(WelcomeScreen.class).display();
        }
    }
}
