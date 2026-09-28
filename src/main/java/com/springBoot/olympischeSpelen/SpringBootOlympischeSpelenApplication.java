package com.springBoot.olympischeSpelen;

import java.security.Principal;

import java.util.Locale;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


import perform.PerformRestWedstrijd;
import service.MyUserDetailsService;

import service.SportService;
import service.SportServiceImpl;
import service.TicketKopenService;
import service.TicketKopenServiceImpl;
import service.WedstrijdService;
import service.WedstrijdServiceImpl;
import validator.OlympischeNummerValidation;

import validator.WedstrijdDatumValidation;
import web.MeasurementInterceptor;

@SpringBootApplication
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class SpringBootOlympischeSpelenApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootOlympischeSpelenApplication.class, args);
		
		try {
			new PerformRestWedstrijd();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@ModelAttribute("username")
	public String populateColors(Principal principal) {
	   return principal.getName();
	 }
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
	   registry.addRedirectViewController("/", "/sporten");
	   registry.addViewController("/403").setViewName("403");
    }

	@Bean
	UserDetailsService myUserDetailsService() {
		return new MyUserDetailsService();
	}


	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		
		
		return slr;
	}
	
	
	
	@Bean 
	DateFormatter dateFormatter() {
		return new DateFormatter();
	}
	
	@Bean
	OlympischeNummerValidation percentValidation() {
		return new OlympischeNummerValidation();
	}
	
	@Bean
	SportService sportService() {
		return new SportServiceImpl();
	}
	

	@Bean
	TicketKopenService ticketService() {
		return new TicketKopenServiceImpl();
	}
	
	
	@Bean
	WedstrijdService wedstrijdService() {
		return new WedstrijdServiceImpl();
	}
	
	
	@Bean
	WedstrijdDatumValidation datumValidation() {
		return new WedstrijdDatumValidation();
	}

	
	@Bean
    SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.put("exception.EntityNotFound", 
        		     "errors/notFound");
        mappings.put("exception.DuplicateException", 
   		     "errors/notFound");
        mappings.put("java.lang.*", 
                "error/notFound");

        r.setDefaultErrorView("errors/notFound");
        r.setExceptionMappings(mappings);
        return r;
    }
	

	
    @Bean
    MeasurementInterceptor measurementInterceptor() {
        return new MeasurementInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(measurementInterceptor()).
        		addPathPatterns("/login").
                addPathPatterns("/sporten").
                addPathPatterns("/tickets**");
    }
}
