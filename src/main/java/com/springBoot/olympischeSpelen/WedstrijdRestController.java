package com.springBoot.olympischeSpelen;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.Wedstrijd;
import service.WedstrijdService;
import utility.PlaatsBeschikbaarhiedResponseRest;


@RestController
@RequestMapping(value = "/api")
public class WedstrijdRestController {
	@Autowired
	private WedstrijdService wedstrijdService;
	
    @GetMapping("/plaatsen/{wedstrijdId}")
    public PlaatsBeschikbaarhiedResponseRest getBeschikbarePlaatsenVoorWedstrijd(@PathVariable Long wedstrijdId) {
        
         Integer aantalPlaatsen = wedstrijdService.beschikbarePlaatsenVoorWedstrijd(wedstrijdId);
         return new PlaatsBeschikbaarhiedResponseRest(aantalPlaatsen);
       
    }

    @GetMapping("/wedstrijden/{sportId}")
    public List<Wedstrijd> getAlleWedstrijdenVanSport(@PathVariable Long sportId) {
    	List<Wedstrijd> wedstrijden =  wedstrijdService.getBySport(sportId);
 
    	return wedstrijden;
    }
}
