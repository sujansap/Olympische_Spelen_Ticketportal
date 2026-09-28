package service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import domain.Sport;
import domain.Stadium;
import domain.Wedstrijd;
import exception.DuplicateException;
import exception.EntityNotFound;
import exception.StadiumNotFoundException;

public interface WedstrijdService {
	
    void saveWedstrijd(Wedstrijd wedstrijd, Long id) throws DuplicateException, StadiumNotFoundException;
	
	List<Wedstrijd> getBySport(Long id);
	
	Wedstrijd getWedstrijd(Long id);
	
	List<Stadium> getStadiums();
	
	Long getSportIdOfWedstrijd(Long wedstrijdId);
	
	boolean wedstijdExistsById(Long wedstrijdId);
	
	Integer beschikbarePlaatsenVoorWedstrijd(Long wedstrijdId);

	
}
