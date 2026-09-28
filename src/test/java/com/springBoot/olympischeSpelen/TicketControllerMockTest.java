package com.springBoot.olympischeSpelen;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.Stadium;
import domain.Ticket;
import domain.Wedstrijd;
import service.TicketKopenService;
import service.WedstrijdService;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketKopenService ticketKopenService;

    @MockBean
    private WedstrijdService wedstrijdService;

    @Mock
    private Principal principal;

    
    @Autowired
    private MessageSource messageSource;
    
    private Wedstrijd wedstrijd;
    private List<Ticket> tickets;

    @BeforeEach
    void setup() {
		Sport football = Sport.builder().id(1L).sportNaam("Football").build();

		Sport basketball = Sport.builder().sportNaam("Basketball").build();
		List<Sport> sporten = List.of(football, basketball);
		Stadium stadium1 = Stadium.builder().stadiumNaam("Parc des Princes").build();

		Stadium stadium2 = Stadium.builder().stadiumNaam("Stade de France").build();
		MyUser user = MyUser.builder().username("nameUser").role(Role.USER).password("test").city("Ghent").build();

		wedstrijd = Wedstrijd.builder().id(1L).sport(football).datumEnAanvangsuur(LocalDateTime.now())
				.olympischNummer1("12345").ticketPrijs(25.0).discipline1("Men's Football").aantalPlaatsen(40)
				.capaciteit(40).stadium(stadium1).build();

		Wedstrijd wedstrijd2 = Wedstrijd.builder().id(2L).sport(football).datumEnAanvangsuur(LocalDateTime.now())
				.olympischNummer1("12346").ticketPrijs(25.0).discipline1("Women's Football").aantalPlaatsen(49)
				.capaciteit(49).stadium(stadium2).build();



		
		
        tickets = List.of(Ticket.builder().id(1L).wedstrijd(wedstrijd).build(), Ticket.builder().id(2L).wedstrijd(wedstrijd).build());

        when(wedstrijdService.getWedstrijd(1L)).thenReturn(wedstrijd);
        when(ticketKopenService.getAlleTicketsVanUser("user")).thenReturn(tickets);
        when(ticketKopenService.hoveelTicketsKanIkNogKopen("user", 1L)).thenReturn(3);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowTickets() throws Exception {
        mockMvc.perform(get("/tickets").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/lijstTickets"))
                .andExpect(model().attributeExists("tickets"))
                .andExpect(model().attribute("tickets", tickets));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowTicketForm() throws Exception {
    	isLimietBereiktTicketKopen(false);
        when(ticketKopenService.isTicketBeschikbaarVoorWedstrijd(1L)).thenReturn(true);
        when(wedstrijdService.getSportIdOfWedstrijd(1L)).thenReturn(1L);
        mockMvc.perform(get("/tickets/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/ticketForm"))
                .andExpect(model().attributeExists("sportId"))
                .andExpect(model().attributeExists("wedstrijdId"))
                .andExpect(model().attributeExists("aantalBeschikbarePlaatsen"))
                .andExpect(model().attributeExists("ticket"))
                .andExpect(model().attribute("sportId", 1L))
                .andExpect(model().attribute("wedstrijdId", 1L))
                .andExpect(model().attribute("aantalBeschikbarePlaatsen", 3));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGeenShowTicketFormVoorAdmin() throws Exception {
    	isLimietBereiktTicketKopen(false);
        when(ticketKopenService.isTicketBeschikbaarVoorWedstrijd(1L)).thenReturn(true);
        when(wedstrijdService.getSportIdOfWedstrijd(1L)).thenReturn(1L);
        mockMvc.perform(get("/tickets/1").principal(principal))
                .andExpect(status().isForbidden());
   
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testToonGeenTicketsBeschikbaar() throws Exception {
    	isLimietBereiktTicketKopen(false);

        mockMvc.perform(get("/tickets/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/geenTickets"));
    }

    private void isLimietBereiktTicketKopen(boolean status) {
    	 when(ticketKopenService.isLimietBereiktTicketKopen("user", 1L)).thenReturn(status);
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowTicketFormGeenTickets() throws Exception {
    	isLimietBereiktTicketKopen(false);
        when(ticketKopenService.isTicketBeschikbaarVoorWedstrijd(1L)).thenReturn(false);

        mockMvc.perform(get("/tickets/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/geenTickets"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 20})
    @WithMockUser(username = "user", roles = {"USER"})
    void testKoopTicketWithDifferentQuantities(int validAantal) throws Exception {
        Long wedstrijdId = 1L;
        Wedstrijd wedstrijd = new Wedstrijd(); 
        wedstrijd.setId(1L);

        Ticket ticket = Ticket.builder().id(1L).wedstrijd(wedstrijd).aantal(validAantal).build();

        when(wedstrijdService.getSportIdOfWedstrijd(wedstrijdId)).thenReturn(wedstrijd.getId());
        doNothing().when(ticketKopenService).koopTicket(any(Ticket.class), eq("user"), eq(wedstrijdId));

        mockMvc.perform(post("/tickets/{id}", wedstrijdId)
                        .with(csrf()) 
                        .principal(principal)
                        .param("aantal", String.valueOf(validAantal))
                        .flashAttr("ticket", ticket))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wedstrijden/" + wedstrijd.getId() + "/details/" + wedstrijdId));

        verify(ticketKopenService, times(1)).koopTicket(any(Ticket.class), eq("user"), eq(wedstrijdId));
    }


    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-22"})
    @WithMockUser(username = "user", roles = {"USER"})
    void testKoopTicketWithValidationErrors(String invalidAantal) throws Exception {
        Long wedstrijdId = 1L;

        Ticket ticket = Ticket.builder().id(1L).wedstrijd(wedstrijd).build();

        when(wedstrijdService.getWedstrijd(wedstrijdId)).thenReturn(wedstrijd);
        when(ticketKopenService.hoveelTicketsKanIkNogKopen("user", wedstrijdId)).thenReturn(3);
        when(ticketKopenService.isTicketBeschikbaarVoorWedstrijd(wedstrijdId)).thenReturn(true);

        mockMvc.perform(post("/tickets/{id}", wedstrijdId)
                        .with(csrf()) 
                        .principal(principal)
                        .param("aantal", invalidAantal)
                        .flashAttr("ticket", ticket))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/ticketForm"))
                .andExpect(model().attributeExists("sportId"))
                .andExpect(model().attributeExists("wedstrijdId"))
                .andExpect(model().attributeExists("aantalBeschikbarePlaatsen"))
                .andExpect(model().attributeExists("ticket"));

        verify(ticketKopenService, times(0)).koopTicket(any(Ticket.class), eq("user"), eq(wedstrijdId));
    }
}
