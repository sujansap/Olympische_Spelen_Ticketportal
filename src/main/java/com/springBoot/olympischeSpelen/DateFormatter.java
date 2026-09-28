package com.springBoot.olympischeSpelen;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

public class DateFormatter implements Formatter<LocalDateTime> {

    @Autowired
    private MessageSource messageSource;

    public DateFormatter() {
        super();
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.format(showFormatter(locale));
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
    	System.out.print("we are here tho ");
        return LocalDateTime.parse(text, formatter(locale));
    }

    private DateTimeFormatter showFormatter(Locale locale) {
        String formatPattern = messageSource.getMessage("date.format.pattern.show", null, locale);

        System.out.println(formatPattern);
        return DateTimeFormatter.ofPattern(formatPattern, locale);
    }
    
    private DateTimeFormatter formatter(Locale locale) {
        String formatPattern = messageSource.getMessage("date.format.pattern", null, locale);
        if (formatPattern == null) {
        	 formatPattern = "yyyy-MM-dd'T'HH:mm:ss"; 
        	 
        }
        System.out.println(formatPattern);
        return DateTimeFormatter.ofPattern(formatPattern, locale);
    }
}