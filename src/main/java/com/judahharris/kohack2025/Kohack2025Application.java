package com.judahharris.kohack2025;

import com.judahharris.kohack2025.ai.AiService;
import com.judahharris.kohack2025.ai.SefariaInitializer;
import com.judahharris.kohack2025.screen.ScreenContext;
import com.judahharris.kohack2025.project.WelcomeScreen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
public class Kohack2025Application implements CommandLineRunner {

	@Autowired
	private ScreenContext screenContext;

    public static void main(String[] args) {
		SpringApplication.run(Kohack2025Application.class, args);
	}

	@Override
	public void run(String... args) throws IOException {
		screenContext.createScreenBean(WelcomeScreen.class).display();
	}
}
