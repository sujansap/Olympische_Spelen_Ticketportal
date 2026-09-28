package com.springBoot.olympischeSpelen;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.Stadium;
import domain.Wedstrijd;
import repository.SportRepository;
import repository.StadiumRepository;
import repository.UserRepository;
import repository.WedstrijdRepository;

@Component
public class InitDataConfig implements CommandLineRunner {

	private PasswordEncoder encoder = new BCryptPasswordEncoder();

	private static final String BCRYPTED_PASWOORD = "$2a$12$JYQJAl6IMCyGKVUOGJbdlu8MV2kwRs7m2nlDUUUVhNSRbYLZkh2cS";
	// string 'paswoord': https://bcrypt-generator.com

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SportRepository sportRepository;

	@Autowired
	private StadiumRepository stadiumRepository;

	
	 @Autowired
	 private WedstrijdRepository wedstrijdRepository; 

	@Override
	public void run(String... args) {

		var user = MyUser.builder().username("nameUser").role(Role.USER).password(BCRYPTED_PASWOORD).city("Ghent")
				.build();
		
		var user2 = MyUser.builder().username("user").role(Role.USER).password(BCRYPTED_PASWOORD).city("Ghent")
				.build();
		var admin = MyUser.builder().username("admin").role(Role.ADMIN).password(encoder.encode("admin")).build();

		List<MyUser> userList = Arrays.asList(admin, user,user2);
		userRepository.saveAll(userList);

		Sport football = Sport.builder().sportNaam("Voetbal").build();

		Sport basketball = Sport.builder().sportNaam("Basketbal").build();

		Sport tennis = Sport.builder().sportNaam("Tennis").build();

		Sport athletics = Sport.builder().sportNaam("Atletiek").build();

		Sport swimming = Sport.builder().sportNaam("Zwemmen").build();

		List<Sport> sportList = Arrays.asList(football, basketball, tennis, athletics, swimming);
		sportRepository.saveAll(sportList);

		Stadium stadium1 = Stadium.builder().stadiumNaam("Parc des Princes").build();

		Stadium stadium2 = Stadium.builder().stadiumNaam("Stade de France").build();

		Stadium stadium3 = Stadium.builder().stadiumNaam("Stade Roland Garros").build();

		Stadium stadium4 = Stadium.builder().stadiumNaam("AccorHotels Arena").build();
		
		List<Stadium> stadiumList = Arrays.asList(stadium1, stadium2, stadium3, stadium4);
		stadiumRepository.saveAll(stadiumList);
		
		
		   Wedstrijd wedstrijd1 = Wedstrijd.builder()
	                .sport(football) 
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 11, 0, 0)) 
	                .olympischNummer1("12345") 
	                .ticketPrijs(25.0) 
	                .discipline1("Mannen Football") 
	                .aantalPlaatsen(40) 
	                .capaciteit(40)
	                .stadium(stadium1)
	                .build();
		   
			
		   Wedstrijd wedstrijd2 = Wedstrijd.builder()
	                .sport(football) 
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 10, 0, 0)) 
	                .olympischNummer1("12346") 
	                .ticketPrijs(25.0) 
	                .discipline1("Vrouwen Football") 
	                .aantalPlaatsen(49)
	                .capaciteit(49)
	                .stadium(stadium2)
	                .build();
		   
		   Wedstrijd wedstrijd3= Wedstrijd.builder()
	                .sport(basketball)
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 2, 0, 0)) 
	                .olympischNummer1("12348") 
	                .ticketPrijs(25.0) 
	                .discipline1("Vrouwen basket")
	                .aantalPlaatsen(49) 
	                .capaciteit(49)
	                .stadium(stadium3)
	                .build();
		   
		   Wedstrijd wedstrijd4= Wedstrijd.builder()
	                .sport(basketball) 
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 1, 0, 0)) 
	                .olympischNummer1("12342") 
	                .ticketPrijs(25.0)
	                .discipline1("Vrouwen basket") 
	                .aantalPlaatsen(49) 
	                .capaciteit(49)
	                .stadium(stadium4)
	                .build();

		   
		   Wedstrijd wedstrijd5= Wedstrijd.builder()
	                .sport(tennis) 
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 7, 29, 0, 0)) 
	                .olympischNummer1("12442") 
	                .ticketPrijs(25.0) 
	                .discipline1("Mannen tennis") 
	                .aantalPlaatsen(49) 
	                .capaciteit(49)
	                .stadium(stadium1)
	                .build();

		   Wedstrijd wedstrijd6= Wedstrijd.builder()
	                .sport(tennis)
	                .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 29, 2, 0)) 
	                .olympischNummer1("12443") 
	                .ticketPrijs(25.0) 
	                .discipline1("Vrouwen tennis") 
	                .aantalPlaatsen(8) 
	                .capaciteit(8)
	                .stadium(stadium2)
	                .build();
	       
		   List<Wedstrijd> wedstrijden = Arrays.asList(wedstrijd1, wedstrijd2,wedstrijd3, wedstrijd4, wedstrijd5, wedstrijd6);
	       wedstrijdRepository.saveAll(wedstrijden);	
	      
	}

}
