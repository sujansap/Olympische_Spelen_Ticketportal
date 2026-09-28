package utility;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class PlaatsBeschikbaarhiedResponseRest {
	
	
	@JsonProperty("aantal_plaatsen_beschikbaar")
    private int aantalPlaatsen;
    
    @JsonCreator
    public PlaatsBeschikbaarhiedResponseRest(@JsonProperty("aantal_plaatsen_beschikbaar") Integer aantalPlaatsen) {
        this.aantalPlaatsen = aantalPlaatsen;
    }
   

}
