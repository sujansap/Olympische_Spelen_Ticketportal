package com.springBoot.olympischeSpelen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import java.security.Principal;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.Stadium;
import domain.Wedstrijd;
import exception.DuplicateException;
import exception.EntityNotFound;
import exception.StadiumNotFoundException;
import service.SportService;
import service.TicketKopenService;
import service.WedstrijdService;
import utility.Message;
import validator.OlympischeNummerValidation;
import validator.WedstrijdDatumValidation;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class WedstrijdControllerMockTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MessageSource messageSource;

	@MockBean
	private WedstrijdService wedstrijdService;

	@MockBean
	private SportService sportService;

	@MockBean
	private TicketKopenService ticketKopenService;

    @Mock
    private Principal principal;

	@Autowired
	private OlympischeNummerValidation olympischeNummerValidation;

	@Autowired
	private WedstrijdDatumValidation wedstrijdDatumValidation;
	
	private List<Wedstrijd> wedstrijden;
	private Wedstrijd wedstrijd;
	
	


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

		System.out.println("this is some test: ");
		System.out.print(LocalDateTime.now());

		wedstrijden = List.of(wedstrijd, wedstrijd2);

	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testShow() throws Exception {

		when(wedstrijdService.getBySport(1L)).thenReturn(wedstrijden);

		mockMvc.perform(get("/wedstrijden/1")).andExpect(status().isOk())
				.andExpect(view().name("wedstrijden/wedstrijden")).andExpect(model().attributeExists("sportId"))
				.andExpect(model().attributeExists("wedstrijdenLijst")).andExpect(model().attribute("sportId", 1L))
				.andExpect(model().attribute("wedstrijdenLijst", wedstrijden));
	}

	@ParameterizedTest
	@ValueSource(longs = {-22L,0L, 22L, 23L, 24L })
	@WithMockUser(username = "user", roles = { "USER" })
	void testToonGeenWedstrijdenVoorSportDieNietBestaat(Long sportId) throws Exception {
		throwSportNietGevondenError(sportId);

		mockMvc.perform(get("/wedstrijden/" + sportId)).andExpect(status().isOk())
				.andExpect(view().name("errors/notFound")).andExpect(model().attributeExists("username"))
				.andExpect(model().attributeExists("message")).andExpect(model().attribute("username", "user"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testShowForm() throws Exception {

		sportCheckSlaagt(1L);

		mockMvc.perform(get("/wedstrijden/1/add")).andExpect(status().isOk())
				.andExpect(view().name("wedstrijden/editWedstrijd")).andExpect(model().attributeExists("sportId"))
				.andExpect(model().attributeExists("wedstrijd")).andExpect(model().attribute("sportId", 1L));
	}

	@ParameterizedTest
	@ValueSource(longs = {-500L,-22L,0L,1L, 22L, 23L, 24L })
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testDoNotShowFormForSportDieNietBestaat(Long sportId) throws Exception {

		throwSportNietGevondenError(sportId);

		mockMvc.perform(get("/wedstrijden/" + sportId + "/add")).andExpect(status().isOk())
				.andExpect(view().name("errors/notFound")).andExpect(model().attributeExists("username"))
				.andExpect(model().attributeExists("message")).andExpect(model().attribute("username", "admin"));
	}
	

	@Test
	@WithMockUser(username = "user", roles = { "User" })
	void testDoNotShowFormToUser() throws Exception {
		geefWedstrijdById(1L);
		sportCheckSlaagt(1L);

		mockMvc.perform(get("/wedstrijden/1/add")).andExpect(status().isForbidden());
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	@WithMockUser(username = "user", roles = { "USER" })
	void testShowDetails(boolean toonPrijs) throws Exception {
		Principal principal = () -> "user";

		Long sportId = 1L;

		geefWedstrijdById(sportId);
		when(ticketKopenService.alTicketGekocht("user", 1L)).thenReturn(true);
		when(ticketKopenService.checkToonPrijs("user", 1L)).thenReturn(toonPrijs);
		sportCheckSlaagt(sportId);

		mockMvc.perform(get("/wedstrijden/" + sportId + "/details/1").principal(principal)).andExpect(status().isOk())
				.andExpect(view().name("wedstrijden/wedstrijdDetails")).andExpect(model().attributeExists("sportId"))
				.andExpect(model().attributeExists("wedstrijd")).andExpect(model().attributeExists("toonPrijs"))
				.andExpect(model().attributeExists("tickets")).andExpect(model().attribute("sportId", 1L))
				.andExpect(model().attribute("wedstrijd", wedstrijd))
				.andExpect(model().attribute("toonPrijs", toonPrijs));
	}

	private void throwSportNietGevondenError(Long id) {
		doThrow(new EntityNotFound("message.notfound.sport")).when(sportService).checkSport(id);
	}
	private void throwWedstrijdNietGevondenError(Long id) {
		doThrow(new EntityNotFound("message.notfound.wedstrijd")).when(wedstrijdService).getWedstrijd(id);
	}

	private void geefWedstrijdById(Long id) {
		when(wedstrijdService.getWedstrijd(id)).thenReturn(wedstrijd);
	}

	private void sportCheckSlaagt(Long id) {
		doNothing().when(sportService).checkSport(id);
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testDoNotShowDetailsIdNietOvereenkomend() throws Exception {
		Principal principal = () -> "user";
		Long wedstrijdId = 1L;
		geefWedstrijdById(wedstrijdId);
		when(ticketKopenService.alTicketGekocht("user", wedstrijdId)).thenReturn(true);
		when(ticketKopenService.checkToonPrijs("user", wedstrijdId)).thenReturn(false);

		throwSportNietGevondenError(22L);

		mockMvc.perform(get("/wedstrijden/22/details/"+wedstrijdId).principal(principal)).andExpect(status().isOk())
				.andExpect(view().name("errors/notFound")).andExpect(model().attributeExists("username"))
				.andExpect(model().attributeExists("message")).andExpect(model().attribute("username", "user"));
	}
	
	

	@ParameterizedTest
	@ValueSource(longs = {-222L,-1L, 0L, 1L, 22L, 23L, 24L })
	@WithMockUser(username = "user", roles = { "USER" })
	void testDoNotShowDetailsWedstrijdBestaatNiet(Long wedstrijdId) throws Exception {
		Principal principal = () -> "user";
			
		sportCheckSlaagt(1L);
		
		when(ticketKopenService.alTicketGekocht("user", wedstrijdId)).thenReturn(true);
		when(ticketKopenService.checkToonPrijs("user", wedstrijdId)).thenReturn(false);

		throwWedstrijdNietGevondenError(wedstrijdId);

		mockMvc.perform(get("/wedstrijden/1/details/"+wedstrijdId).principal(principal)).andExpect(status().isOk())
				.andExpect(view().name("errors/notFound")).andExpect(model().attributeExists("username"))
				.andExpect(model().attributeExists("message")).andExpect(model().attribute("username", "user"));
	}
	
	
	
	
	//post testen 

	@ParameterizedTest
    @CsvSource({
        "2024-07-28T11:30:26, 12345, 12234, 10, test, niet, 10, 1",
        "2024-08-01T10:00:00, 54321, 54322, 20, voetbal, test, 20, 2",
        "2024-08-10T10:00:00, 54321, 54322, 20, voetbal, test, 20, 2",
        "2024-08-10T12:59:00, 54321, 54322, 20, voetbal, test, 2, 1",
        "2024-08-10T22:00:00, 54321, 54322, 20, voetbal, basketbal, 49, 1",
        "2024-08-10T19:00:00, 54321, 54322, 20, voetbal, , 49, 1",
        "2024-08-10T15:00:00, 54321, 54322, 20, voetbal, , 49, 1",
        "2024-08-10T12:00:00, 54321, 54325, 49, test, rest, 149, 1",
        "2024-08-10T12:00:00, 54321, 54325, 20, test, rest, 49, 1",
        "2024-08-11T12:23:59, 54321, 54325, 20, test, rest, 49, 1"
    })
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testUpdate_Success(String datumEnAanvangsuur, String olympischNummer1, String olympischNummer2,
                                   String aantalPlaatsen, String discipline1, String discipline2, String ticketPrijs, 
                                   String hulpStadiumId) throws Exception {

        LocalDateTime datumEnAanvangsuurParsed = LocalDateTime.parse(datumEnAanvangsuur, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        int aantalPlaatsenInt = Integer.parseInt(aantalPlaatsen);
        double ticketPrijsDouble = Double.parseDouble(ticketPrijs);
        Long hulpStadiumIdLong = Long.parseLong(hulpStadiumId);

        Wedstrijd wedstrijd_post = Wedstrijd.builder()
                .datumEnAanvangsuur(datumEnAanvangsuurParsed)
                .olympischNummer1(olympischNummer1)
                .aantalPlaatsen(aantalPlaatsenInt)
                .ticketPrijs(ticketPrijsDouble)
                .stadium(null) 
                .build();

        doNothing().when(wedstrijdService).saveWedstrijd(wedstrijd_post, 1L);;

        mockMvc.perform(post("/wedstrijden/1/add")
                        .with(csrf())
                        .param("datumEnAanvangsuur", datumEnAanvangsuur)
                        .param("olympischNummer1", olympischNummer1)
                        .param("olympischNummer2", olympischNummer2)
                        .param("aantalPlaatsen", aantalPlaatsen)
                        .param("discipline1", discipline1)
                        .param("discipline2", discipline2)
                        .param("ticketPrijs", ticketPrijs)
                        .param("hulpStadiumId", hulpStadiumId))
                .andExpect(redirectedUrl("/wedstrijden/1"));

        verify(wedstrijdService, times(1)).saveWedstrijd(any(Wedstrijd.class), eq(1L));
    }

	@ParameterizedTest
    @CsvSource({
    	"2024-08-10T07:00:00, 04321, 12345,  10, basketbal, basketbal mannen, 1, olympischNummer1",
    	"2024-08-10T07:00:00, 14321, 6522,  10, basketbal, basketbal mannen, 1, olympischNummer1",
    	"2024-08-01T10:00:00, 54321, 54321,  10, voetbal, basketbal, 1, olympischNummer1",
    	"2024-08-01T10:00:00, 54321, 54322,  10, basketbal, basketbal, 1, discipline1",
    	"2024-08-01T10:00:00, 54321, 54322,  -10, voetbal, basketbal, 1, aantalPlaatsen",
    	"2024-08-01T10:00:00, 54321, 54322,  0, voetbal, basketbal, 1, aantalPlaatsen",
    	"2024-08-01T10:00:00, 54321, 54322,  -1, voetbal, basketbal, 1, aantalPlaatsen",
    	"2024-08-01T10:00:00, 54321, 54322,  -222, voetbal, basketbal, 1, aantalPlaatsen",
    	"2024-08-01T10:00:00, 54321, 54322,  10, voetbal, basketbal, 0, ticketPrijs",
    	"2024-08-01T10:00:00, 54321, 54322,  10, voetbal, basketbal, -1, ticketPrijs",
    	"2024-08-01T10:00:00, 54321, 54322,  10, voetbal, basketbal, , ticketPrijs",
        "2024-08-01T10:00:00, 14321, 14322,  10, voetbal, basketbal, 150, ticketPrijs",
    	"2024-08-12T10:00:00, 54321, 54322,  10, basketbal, basketbal mannen, 1, datumEnAanvangsuur",
    	"2024-08-12T10:00:00, 54321, 54322,  10, basketbal, basketbal mannen, 1, datumEnAanvangsuur",
    	"2024-07-26T07:00:00, 54321, 54322,  10, basketbal, basketbal mannen, 1, datumEnAanvangsuur",
    	"2024-07-26T07:59:59, 54321, 54322,  10, basketbal, basketbal mannen, 1, datumEnAanvangsuur",
    })
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testUpdate_ValidationError(String datumEnAanvangsuur, String olympischNummer1, String olympischNummer2,
                                           String aantalPlaatsen, String discipline1,
                                           String discipline2, String ticketPrijs,String foutveld) throws Exception {
        mockMvc.perform(post("/wedstrijden/1/add")
                        .with(csrf())
                        .param("datumEnAanvangsuur" , datumEnAanvangsuur)
                        .param("olympischNummer1", olympischNummer1)
                        .param("olympischNummer2", olympischNummer2 )
                        .param("aantalPlaatsen", aantalPlaatsen)
                        .param("discipline1", discipline1 )
                        .param("discipline2", discipline2)
                        .param("ticketPrijs", ticketPrijs))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeHasFieldErrors("wedstrijd", foutveld))
                .andExpect(view().name("wedstrijden/editWedstrijd"));
    }

    
    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testUpdate_DuplicateException() throws Exception {
        doThrow(new DuplicateException("error.duplicate")).when(wedstrijdService).saveWedstrijd(any(), anyLong());

        mockMvc.perform(post("/wedstrijden/1/add")
        				.with(csrf()) 
                        .param("datumEnAanvangsuur", "2024-08-01T10:00")
                        .param("olympischNummer1", "12345")
                        .param("aantalPlaatsen", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("wedstrijden/editWedstrijd"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testUpdate_StadiumNotFoundException() throws Exception {
        doThrow(new StadiumNotFoundException("error.stadium.notfound")).when(wedstrijdService).saveWedstrijd(any(), anyLong());

        mockMvc.perform(post("/wedstrijden/1/add")
        				.with(csrf()) 
                        .param("datumEnAanvangsuur", "2024-08-01T10:00")
                        .param("olympischNummer1", "12345")
                        .param("aantalPlaatsen", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("wedstrijden/editWedstrijd"));
    }
	

}
