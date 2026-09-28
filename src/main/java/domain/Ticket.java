package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access=AccessLevel.PUBLIC)
@Getter @Setter
@EqualsAndHashCode(of={"wedstrijd", "aantal", "aangekochtOp"})
@Entity
@Builder

@NamedQueries({ 
	@NamedQuery(name = "Ticket.aantalTicketsGekochtDoorUserVoorEenWedstrijd", 
	query = "SELECT sum(t.aantal) FROM Ticket t WHERE t.user = :user and t.wedstrijd = :wedstrijd"),
	@NamedQuery(name = "Ticket.aantalTicketsGekochtDoorUserTotaal", 
	query = "SELECT sum(t.aantal) FROM Ticket t WHERE t.user = :user"),
	@NamedQuery(name = "Ticket.geefAlleTickettenGesorteerd", 
	query = "SELECT t FROM Ticket t WHERE t.user = :user")
})
@AllArgsConstructor
public class Ticket  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	private Wedstrijd wedstrijd;
	
	@ManyToOne
	private MyUser user;
	
    @Min(value = 1, message = "{error.aantalTicketsGekocht.min}")
    @Max(value = 20, message = "{error.aantalTicketsGekocht.max}")
	private Integer aantal;
    
    
	@CreationTimestamp
	private LocalDateTime aangekochtOp;
	
}
