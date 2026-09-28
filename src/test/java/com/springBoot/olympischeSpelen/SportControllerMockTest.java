package com.springBoot.olympischeSpelen;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.Stadium;
import domain.Ticket;
import domain.Wedstrijd;
import repository.UserRepository;
import service.SportService;
import service.TicketKopenService;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class SportControllerMockTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SportService sportService;

	@MockBean
	private TicketKopenService ticketKopenService;

	Sport football = Sport.builder().sportNaam("Football").build();

	Sport basketball = Sport.builder().sportNaam("Basketball").build();
	List<Sport> sporten = List.of(football, basketball);
	Stadium stadium1 = Stadium.builder().stadiumNaam("Parc des Princes").build();

	Stadium stadium2 = Stadium.builder().stadiumNaam("Stade de France").build();
	MyUser user = MyUser.builder().username("nameUser").role(Role.USER).password("test").city("Ghent")
			.build();
	
	   Wedstrijd wedstrijd1 = Wedstrijd.builder()
             .sport(football)
             .datumEnAanvangsuur(LocalDateTime.of(2024, 8, 11, 0, 0)) 
             .olympischNummer1("12345") 
             .ticketPrijs(25.0)
             .discipline1("Men's Football") 
             .aantalPlaatsen(40) 
             .capaciteit(40)
             .stadium(stadium1)
             .build();
	   
		
	   Wedstrijd wedstrijd2 = Wedstrijd.builder()
             .sport(football) 
             .datumEnAanvangsuur(LocalDateTime.now()) 
             .olympischNummer1("12346") 
             .ticketPrijs(25.0) 
             .discipline1("Women's Football") 
             .aantalPlaatsen(49) 
             .capaciteit(49)
             .stadium(stadium2)
             .build();
	
	
	Ticket ticket1 = Ticket.builder().wedstrijd(wedstrijd1).user(user).aantal(2).build();

	Ticket ticket2 = Ticket.builder().wedstrijd(wedstrijd1).user(user).aantal(3).build();

	@BeforeEach
	public void setup() {

		when(sportService.getSporten()).thenReturn(sporten);
		
	}

	@BeforeEach

	@Test
	public void loginGet() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	public void accessDeniedPageGet() throws Exception {
		mockMvc.perform(get("/403")).andExpect(status().isOk()).andExpect(view().name("403"));
	}

	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testAdminAccess() throws Exception {
		mockMvc.perform(get("/sporten")).andExpect(status().isOk()).andExpect(view().name("sports"))
				.andExpect(model().attributeExists("sportLijst")).andExpect(model().attribute("sportLijst", sporten));
	}

	@ParameterizedTest
    @ValueSource(ints = {-1,0,1,2,2,1000})
    @WithMockUser(username = "nameUser", roles = { "USER" })
    public void testUserAccess(int aantal) throws Exception {
        when(ticketKopenService.getHoveelAlGekochtTotaal("nameUser")).thenReturn(aantal);

        mockMvc.perform(get("/sporten"))
            .andExpect(status().isOk())
            .andExpect(view().name("sports"))
            .andExpect(model().attributeExists("sportLijst"))
            .andExpect(model().attribute("sportLijst", sporten))
            .andExpect(model().attributeExists("aantalGekochteTicket"))
            .andExpect(model().attribute("aantalGekochteTicket", aantal));
    }
}
