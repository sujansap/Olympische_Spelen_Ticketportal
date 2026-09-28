package service;

import java.util.List;

import domain.MyUser;
import domain.Ticket;
import domain.Wedstrijd;
import jakarta.transaction.Transactional;

public interface TicketKopenService {


	Integer getAantalBeschikbareplaatsen(Long wedstrijdId);
	
	@Transactional
	void koopTicket(Ticket ticket, String username, Long wedstrijdId);
	Integer getHoveelAlGekochtVoorEenWedstrijd(String username, Long wedstrijdId);
	Integer getHoveelAlGekochtTotaal(String username);
	List<Ticket> getAlleTicketenVoorUserVoorWedstrijd(String username, Long wedstrijdId);
	Integer hoveelTicketsKanIkNogKopen(String name, Long id);
	List<Ticket> getAlleTicketsVanUser(String username);
	
	
	boolean alTicketGekocht(String username, Long wedstrijdId);
	boolean checkToonPrijs(String username, Long wedstrijdId);
	boolean isTicketBeschikbaarVoorWedstrijd(Long id);
	boolean isLimietBereiktTicketKopen(String username, Long id);
}
