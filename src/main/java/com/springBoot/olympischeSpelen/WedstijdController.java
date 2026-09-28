package com.springBoot.olympischeSpelen;

import java.security.Principal;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import domain.Constante;

import domain.Stadium;
import domain.Ticket;
import domain.Wedstrijd;

import exception.DuplicateException;
import exception.EntityNotFound;
import exception.StadiumNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import repository.StadiumRepository;
import service.SportService;
import service.TicketKopenService;
import service.WedstrijdService;
import utility.Message;
import validator.OlympischeNummerValidation;
import validator.WedstrijdDatumValidation;

@Controller
@RequestMapping("/wedstrijden")
public class WedstijdController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private WedstrijdDatumValidation wedstrijdDatumValidation;

	@Autowired
	private WedstrijdService wedstrijdService;

	@Autowired
	private SportService sportService;

	@Autowired
	private OlympischeNummerValidation olympischeNummerValidation;

	@Autowired
	private TicketKopenService ticketKopenService;

	@ModelAttribute("username")
	public String populateColors(Principal principal) {
		return principal.getName();
	}

	@GetMapping(value = "/{id}")
	public String show(@PathVariable Long id, Model model) {
		model.addAttribute("sportId", id);

		// hier wordt in service gekeken of sport bestaat als niet bestaat wordt er in
		// service zelf exception
		// gegooid die dan behandelt wordt hier met @ExceptionHandler behandelt
		// drm hier geen if nodig
		sportService.checkSport(id);

		model.addAttribute("wedstrijdenLijst", wedstrijdService.getBySport(id));
		return "wedstrijden/wedstrijden";
	}

	@GetMapping(value = "/{id}/add")
	public String showForm(@PathVariable Long id, Model model) {
		model.addAttribute("sportId", id);
		Wedstrijd wedstrijd = new Wedstrijd();

		sportService.checkSport(id);

		
		model.addAttribute("wedstrijd", wedstrijd);

		
		return "wedstrijden/editWedstrijd";
	}

	@ModelAttribute("stadiums")
	public List<Stadium> populateProducts() {
		return wedstrijdService.getStadiums();
	}

	@ExceptionHandler(DuplicateException.class)
	public String handleCustomException(DuplicateException ex, Model model) {
		
		model.addAttribute("errorMessage", "something went wrong");
		return "wedstrijden/editWedstrijd";
	}

	@ExceptionHandler(EntityNotFound.class)
	public ModelAndView handleCustomException(EntityNotFound ex, Locale locale, Principal principal) {
		ModelAndView model = new ModelAndView("errors/notFound");

		model.addObject("username", principal.getName());
		model.addObject("message",
				new Message("error", messageSource.getMessage(ex.getMessage(), new Object[] {}, locale)));
		return model;
	}

	@PostMapping(value = "/{id}/add")
	public String update(@PathVariable Long id, @Valid Wedstrijd wedstrijd, BindingResult bindingResult, Model model,
			Locale locale) {
		
		
		model.addAttribute("sportId", id);
		
		wedstrijdDatumValidation.validate(wedstrijd.getDatumEnAanvangsuur(), bindingResult);

		olympischeNummerValidation.validate(wedstrijd, bindingResult);
		
		if (bindingResult.hasErrors()) {
			
			// tweede parameter null, want je hebben geen parameters
			model.addAttribute("message",
					new Message("error", messageSource.getMessage("wedstrijd_save_fail", new Object[] {}, locale))); 
			
			return "wedstrijden/editWedstrijd";
		}
		
		
		
		try {
			wedstrijdService.saveWedstrijd(wedstrijd, id);
		} catch (DuplicateException | StadiumNotFoundException e) {
			model.addAttribute("message",
					new Message("error", messageSource.getMessage(e.getMessage(), new Object[] {}, locale))); 
			return "wedstrijden/editWedstrijd";
		}

		return "redirect:/wedstrijden/" + id;
	}

	@GetMapping(value = "{sportId}/details/{wedstrijdId}")
	public String showDetails(@PathVariable Long sportId, @PathVariable Long wedstrijdId, Model model,
			Principal principal, Locale locale) {
		
		sportService.checkSport(sportId);
		
		model.addAttribute("sportId", sportId);
		
		
		Wedstrijd wedstrijd = wedstrijdService.getWedstrijd(wedstrijdId);
		
		if(wedstrijd.getSport().getId() != sportId) {
			model.addAttribute("message",
					new Message("error", messageSource.getMessage("message.ongeldig.combinatieSportWedstrijdId", new Object[] {}, locale)));
			
			return "errors/notFound";
		}

		boolean toonPrijs = true;

		if (isAdmin()) {
			model.addAttribute("wedstrijd", wedstrijd);
			model.addAttribute("toonPrijs", toonPrijs);
			return "wedstrijden/wedstrijdDetails";
		}

		toonPrijs = ticketKopenService.checkToonPrijs(principal.getName(), wedstrijdId);
		
		model.addAttribute("tickets", null); // als nog geen tickets gekocht door gebruiker
		if (ticketKopenService.alTicketGekocht(principal.getName(), wedstrijdId)) {

			List<Ticket> tickets = ticketKopenService.getAlleTicketenVoorUserVoorWedstrijd(principal.getName(),
					wedstrijdId);
			model.addAttribute("tickets", tickets);

		}

		model.addAttribute("toonPrijs", toonPrijs);
		model.addAttribute("wedstrijd", wedstrijd);

		return "wedstrijden/wedstrijdDetails";
	}

	private boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
	}

}
