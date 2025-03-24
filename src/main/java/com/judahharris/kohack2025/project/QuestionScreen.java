package com.judahharris.kohack2025.project;

import com.judahharris.kohack2025.ai.AiService;
import com.judahharris.kohack2025.auth.UserRepository;
import com.judahharris.kohack2025.model.User;
import com.judahharris.kohack2025.screen.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QuestionScreen extends Screen {

    @Autowired
    private AiService aiService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void display() {
        User user = getScreenContext().getUser();
        String input = promptNonEmptyString("Ask me a question: ");
        getPrintStream().println("Thinking...");
        String response = aiService.runLLMQuery(input, user.getContext());
        getPrintStream().println(response);
        user.getQuestions().add(new User.Question(input.trim(), response.trim()));
        userRepository.save(user);
        if (promptYesNoBoolean("Would you like to ask another question? (yes/no)")) {
            display();
        }
        screenContext.createScreenBean(WelcomeScreen.class).display();
    }
}
