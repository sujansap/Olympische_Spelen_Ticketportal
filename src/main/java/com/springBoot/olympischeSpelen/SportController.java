package com.springBoot.olympischeSpelen;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import repository.SportRepository;
import service.SportService;
import service.TicketKopenService;
import validator.OlympischeNummerValidation;


@Controller
@RequestMapping("/sporten")
public class SportController {
	@Autowired
	private SportService sportService;
		
	@Autowired
	private TicketKopenService ticketKopenService;
	
	@Autowired
	private OlympischeNummerValidation olympischeNummerValidation;
	
	@ModelAttribute("username")
	public String populateColors(Principal principal) {
	   return principal.getName();
	 }
	
	@GetMapping
	public String showOverview(Model model, Principal principal) {
		model.addAttribute("sportLijst", sportService.getSporten());
		int aantalGekochteTicket = ticketKopenService.getHoveelAlGekochtTotaal(principal.getName());
		model.addAttribute("aantalGekochteTicket", aantalGekochteTicket);
		return "sports";
	}
	

	 

	
}
