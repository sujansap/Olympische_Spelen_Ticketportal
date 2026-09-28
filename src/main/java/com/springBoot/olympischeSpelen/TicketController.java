package com.springBoot.olympischeSpelen;

import java.security.Principal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import domain.Constante;
import domain.Sport;
import domain.Ticket;
import domain.Wedstrijd;
import exception.EntityNotFound;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import service.TicketKopenService;
import service.WedstrijdService;
import service.WedstrijdServiceImpl;
import utility.Message;

@Controller
@RequestMapping("/tickets")
public class TicketController {

	@Autowired
	private TicketKopenService ticketKopenService;
	
	@Autowired
	private WedstrijdService wedstrijdService;
	
	
	@Autowired
	private MessageSource messageSource;
	
	
	@ModelAttribute("username")
	public String populateColors(Principal principal) {
		return principal.getName();
	}
	
	@GetMapping
	public String show(Model model, Principal principal) {
		List<Ticket> tickets = ticketKopenService.getAlleTicketsVanUser(principal.getName());
		model.addAttribute("tickets", tickets);
		
		return "tickets/lijstTickets";
	}
	
	@ExceptionHandler(EntityNotFound.class)
	public ModelAndView handleCustomException(EntityNotFound ex, Locale locale, Principal principal) {
		ModelAndView model = new ModelAndView("errors/notFound");

		model.addObject("username", principal.getName());
		model.addObject("message",
				new Message("error", messageSource.getMessage(ex.getMessage(), new Object[] {}, locale)));
		return model;
	}

	@GetMapping(value = "/{id}")
	public String showForm(@PathVariable Long id, Model model, Principal principal) {
		
		model.addAttribute("sportId", wedstrijdService.getSportIdOfWedstrijd(id));
		
		
		model.addAttribute("wedstrijdId", id);
		if(ticketKopenService.isLimietBereiktTicketKopen(principal.getName(), id))
			return "tickets/limietBereiktTickets";
		
		
		if(!ticketKopenService.isTicketBeschikbaarVoorWedstrijd(id)) 
			return "tickets/geenTickets";
		
		
		Integer aantalUserNogKanKopen = ticketKopenService.hoveelTicketsKanIkNogKopen(principal.getName(), id);
		

		model.addAttribute("aantalBeschikbarePlaatsen",aantalUserNogKanKopen);
		model.addAttribute("ticket", new Ticket());
		model.addAttribute("wedstrijdNr", id);
		
		return "tickets/ticketForm";
	}
	
	
	@PostMapping(value = "/{id}")
	public String koop(@PathVariable Long id, @Valid  @ModelAttribute("ticket")  Ticket ticket,   
			BindingResult bindingResult, Model model,Principal principal,RedirectAttributes ra ) {  
		
		
		if(bindingResult.hasErrors()) {
			return showForm(id, model, principal);
		}
										//zal error gooien ook als je voor ticket probeert te kopen voor wedstrijd die niet bestaat
		Long sportId = wedstrijdService.getSportIdOfWedstrijd(id);
		
		ticketKopenService.koopTicket(ticket, principal.getName(), id);
		
		ra.addFlashAttribute("ticketGekocht", ticket.getAantal());
		
		return "redirect:/wedstrijden/" + sportId + "/details/" + id;
		
	}

}





