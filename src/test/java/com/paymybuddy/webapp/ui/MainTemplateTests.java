package com.paymybuddy.webapp.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTemplateTests {

	private TemplateEngine templateEngine;

	@BeforeEach
	void setUp() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);

		ClassLoaderTemplateResolver staticResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("static/css/");
		templateResolver.setSuffix(".css");
		templateResolver.setTemplateMode(TemplateMode.CSS);

		templateEngine = new TemplateEngine();
		templateEngine.addTemplateResolver(templateResolver);
		templateEngine.addTemplateResolver(staticResolver);
	}

	@Test
	@DisplayName("Header should not display for anonymous user")
	void givenAnonymousUser_whenNavigatingPages_thenHeaderNotDisplay() {
		Context context = new Context();
		context.setVariable("authentication", null);

		String renderedHtml = templateEngine.process("main-template", context);

		assertThat(renderedHtml).doesNotContain("<header>");
	}
}
