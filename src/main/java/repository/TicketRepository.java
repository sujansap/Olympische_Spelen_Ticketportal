package repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import domain.MyUser;
import domain.Ticket;
import domain.Wedstrijd;

public interface TicketRepository  extends CrudRepository<Ticket, Long>{
	
	List<Ticket> findByWedstrijd(Wedstrijd wedstrijd);
	List<Ticket> findByUser(MyUser user);
	List<Ticket> findByUserAndWedstrijdOrderByAangekochtOpAsc(MyUser user,Wedstrijd wedstrijd);
	Integer aantalTicketsGekochtDoorUserVoorEenWedstrijd(@Param("user")MyUser user, @Param("wedstrijd") Wedstrijd wedstrijd);
	Integer aantalTicketsGekochtDoorUserTotaal(@Param("user")MyUser user);
}
