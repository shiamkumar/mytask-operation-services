package com.ghx.api.operations.messagesource;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Rajasekar Jayakumar
 *
 */

@Component
public class CustomMessageSource {

	private static MessageSource messageSource;

	@Autowired
	CustomMessageSource(MessageSource messageSource) {
		CustomMessageSource.messageSource = messageSource;
	}

	public static String getMessage(String msg, Object... args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(msg, args, locale);
	}

}
