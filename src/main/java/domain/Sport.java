package domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode(of="sportNaam")
@Entity
@Builder
@AllArgsConstructor
public class Sport  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank
    @Column(unique=true)
    @Size(min = 1, max = 20)
    private String sportNaam;
	
    @OneToMany(mappedBy="sport")
	private List<Wedstrijd> wedstrijden;

    public Sport(String name) {
    	sportNaam = name;
    }
   

}
