package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import utility.LocalDateTimeDeserializer;
import utility.LocalDateTimeSerializer;
import utility.SportSerializer;
import utility.StadiumSerializer;

@Entity
@Builder
@AllArgsConstructor
@Getter @Setter
@ToString
@NoArgsConstructor(access=AccessLevel.PUBLIC)
@EqualsAndHashCode(of="olympischNummer1")
@NamedQueries({ 
	@NamedQuery(name = "Wedstrijd.aantalBeschikbarePlaatsen", 
	query = "SELECT w.aantalPlaatsen FROM Wedstrijd w WHERE w.id = :wedstrijdId") })
public class Wedstrijd  implements Serializable{
		private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
		@JsonSerialize(using = SportSerializer.class)
	    @ManyToOne
	    private Sport sport;
	    
		@JsonIgnore
	    @OneToMany(mappedBy="wedstrijd")
	    private List<Ticket> tickets;
	    
	    @JsonSerialize(using = StadiumSerializer.class)
	    @ManyToOne
	    private Stadium stadium;
		
	    @JsonIgnore
	    @Transient
	    private Long hulpStadiumId;
	    
	    
	    @JsonSerialize(using = LocalDateTimeSerializer.class)
	    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
	    private LocalDateTime datumEnAanvangsuur;
		

	    @Column(unique=true)
	    private String olympischNummer1;

	
	    private String olympischNummer2;
	    
	
	    private Double ticketPrijs;


	    private String discipline1;

	    private String discipline2;
	    
	    @JsonProperty("aantal_plaatsen")
	    @NotNull(message="{error.aantalPlaatsen.notNull}")
	    @Min(value = 0, message = "{error.aantalPlaatsen.min}")
	    @Max(value = 49, message = "{error.aantalPlaatsen.max}")
	    @Setter(AccessLevel.NONE)
	    private Integer aantalPlaatsen;
	    
	 
	    private Integer capaciteit;
	    
	    public void setAantalPlaatsen(Integer aantalPlaatsen) {
	    	capaciteit = aantalPlaatsen;
	    	this.aantalPlaatsen = aantalPlaatsen;
	    	
	    }
	    
	    public void koopTicket(Integer aantal) {
	    	if(aantal <= Constante.MIN_AANTAL_PLAATSEN || aantal >= aantalPlaatsen || aantal >= Constante.MIN_AANTAL_PLAATSEN) {
	    		
	    	}
	    	
	    	aantalPlaatsen -= aantal;
	    }
	      
	
	    
}
