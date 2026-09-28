package domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Getter @Setter
@EqualsAndHashCode(of="stadiumNaam")
@Entity
@Builder
@AllArgsConstructor()
public class Stadium  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank
    @Size(min = 1, max = 20)
    private String stadiumNaam;
	
	@OneToMany(mappedBy="stadium")
	private List<Wedstrijd> wedstrijden;
 
    public Stadium(String name) {
    	stadiumNaam = name;
    }

}
