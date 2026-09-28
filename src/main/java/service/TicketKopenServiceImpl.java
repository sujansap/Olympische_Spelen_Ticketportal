package service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import domain.Constante;
import domain.MyUser;
import domain.Ticket;
import domain.Wedstrijd;
import jakarta.transaction.Transactional;
import repository.TicketRepository;
import repository.UserRepository;
import repository.WedstrijdRepository;

public class TicketKopenServiceImpl implements TicketKopenService{
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private WedstrijdRepository wedstrijdRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Integer getAantalBeschikbareplaatsen(Long wedstrijdId) {
		Integer aantal = wedstrijdRepository.aantalBeschikbarePlaatsen(wedstrijdId);
		if(aantal != null) {
			return aantal;
		}
		return 0;
		
	}

	@Transactional
	@Override
	public void koopTicket(Ticket ticket, String username, Long wedstrijdId) {
		MyUser user = userRepository.findByUsername(username);
		Optional<Wedstrijd> wedstrijd = wedstrijdRepository.findById(wedstrijdId);
		
		if(user != null && wedstrijd.isPresent() && ticket != null) {
			Wedstrijd w = wedstrijd.get();
			ticket.setId(null);
			ticket.setUser(user);
			ticket.setWedstrijd(w);
			ticketRepository.save(ticket);
			
			w.koopTicket(ticket.getAantal());
			
		}
		
	}

	@Override
	public Integer getHoveelAlGekochtVoorEenWedstrijd(String username, Long wedstrijdId) {
		MyUser user = userRepository.findByUsername(username);
		Optional<Wedstrijd> wedstrijd = wedstrijdRepository.findById(wedstrijdId);
		if(user != null && wedstrijd.isPresent()) {
			Integer aantal = ticketRepository.aantalTicketsGekochtDoorUserVoorEenWedstrijd(user, wedstrijd.get());
			if(aantal==null)
				return 0;
			return aantal;
		}
		
		return 0;
	}
	
	@Override
	public List<Ticket> getAlleTicketenVoorUserVoorWedstrijd(String username,Long wedstrijdId ){
		MyUser user = userRepository.findByUsername(username);
		Optional<Wedstrijd> wedstrijd = wedstrijdRepository.findById(wedstrijdId);
		
		if(user != null && wedstrijd.isPresent()) {
			return ticketRepository.findByUserAndWedstrijdOrderByAangekochtOpAsc(user, wedstrijd.get());
		}
	
		return null;
		
	}

	@Override
	public Integer getHoveelAlGekochtTotaal(String username) {
		MyUser user = userRepository.findByUsername(username);
	
		if(user != null) {
			Integer aantal = ticketRepository.aantalTicketsGekochtDoorUserTotaal(user);
			if(aantal==null)
				return 0;
			return aantal;
		}
		
		return 0;
	}
	
	@Override
	public Integer hoveelTicketsKanIkNogKopen(String username, Long id) {
		//hoeveel er beschikbaar zijn voor een wedstrijd
		Integer aantalBeschikbareTickets = getAantalBeschikbareplaatsen(id);
		//hoeveel de ingelogde user al heeft gekocht
		Integer aantalReedsGekochtVoorWedstrijd = getHoveelAlGekochtVoorEenWedstrijd(username, id);
		
		//user mag Constante.TICKET_LIMIT_MAX - aantalReedsGekochtVoorWedstrijd zoveel tickets nog kopen voor wedstrijd
		Integer aantalUserNogKanKopen = Constante.TICKET_LIMIT_MAX - aantalReedsGekochtVoorWedstrijd;
		
		Integer aantalInTotaalGekocht = getHoveelAlGekochtTotaal(username);
		
		//aantal user nog mag kopen globaal gezien voor alle wedstrijden
		Integer globaalAantalUserNogKanKopen = Constante.TICKET_LIMIT_MAX_TOTAAL - aantalInTotaalGekocht;
		
		//minimum van alle 3 is wat user nog kan kopen
		return  min(aantalUserNogKanKopen, aantalBeschikbareTickets, globaalAantalUserNogKanKopen);
	}
	
	public static int min(int a, int b, int c) {
	    return Math.min(Math.min(a, b), c);
	}

	@Override
	public List<Ticket> getAlleTicketsVanUser(String username) {
		MyUser user = userRepository.findByUsername(username);
		
		if(user!=null) {
			List<Ticket> tickets = ticketRepository.findByUser(user);
			
			tickets.sort(Comparator.<Ticket, String>comparing(ticket -> ticket.getWedstrijd().getSport().getSportNaam())
				    .thenComparing(ticket -> ticket.getWedstrijd().getDatumEnAanvangsuur()));
			return tickets;
		}
		return null;
	}

	
	@Override
	public boolean checkToonPrijs(String username, Long wedstrijdId) {
		// Integer aantalReedsGekochteTicketen, Integer aantalReedsGekochteTicketenTotaal
		Integer aantalReedsGekochteTicketen =  getHoveelAlGekochtVoorEenWedstrijd(username, wedstrijdId);
    	Integer aantalReedsGekochteTicketenTotaal = getHoveelAlGekochtTotaal(username);
	    Integer aantalBeschikbareTickets = getAantalBeschikbareplaatsen(wedstrijdId);
	    
	    if (aantalBeschikbareTickets <= Constante.TICKET_LIMIT_MIN 
	    		|| aantalReedsGekochteTicketen >= Constante.TICKET_LIMIT_MAX
	    		|| aantalReedsGekochteTicketenTotaal >= Constante.TICKET_LIMIT_MAX_TOTAAL) {
	        return false;
	    }
	    
	    return true;
	}
	
	@Override
	public boolean alTicketGekocht(String username, Long wedstrijdId) {
    	Integer aantalReedsGekochteTicketen =  getHoveelAlGekochtVoorEenWedstrijd(username, wedstrijdId);

		return aantalReedsGekochteTicketen > Constante.TICKET_LIMIT_MIN;
	}
	

	@Override
	public boolean isLimietBereiktTicketKopen(String username, Long id) {
		Integer aantalReedsGekochtVoorWedstrijd = getHoveelAlGekochtVoorEenWedstrijd(username, id);
		Integer aantalInTotaalGekocht = getHoveelAlGekochtTotaal(username);

		return (aantalReedsGekochtVoorWedstrijd >= Constante.TICKET_LIMIT_MAX 
				|| aantalInTotaalGekocht >= Constante.TICKET_LIMIT_MAX_TOTAAL);
			
	}
	@Override
	public boolean isTicketBeschikbaarVoorWedstrijd(Long id) {
		Integer aantalBeschikbareTickets = getAantalBeschikbareplaatsen(id);
		return aantalBeschikbareTickets > Constante.TICKET_LIMIT_MIN;
	}
}
