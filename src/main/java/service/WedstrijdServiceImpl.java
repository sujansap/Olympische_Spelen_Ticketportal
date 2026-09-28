package service;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import domain.Constante;
import domain.Sport;
import domain.Stadium;
import domain.Wedstrijd;
import exception.DuplicateException;
import exception.EntityNotFound;
import exception.StadiumNotFoundException;
import repository.SportRepository;
import repository.StadiumRepository;
import repository.WedstrijdRepository;

public class WedstrijdServiceImpl implements WedstrijdService {

	@Autowired
	private WedstrijdRepository wedstrijdRepository;

	@Autowired
	private SportRepository sportRepository;

	@Autowired
	private StadiumRepository stadiumRepository;

	@Override
	public void saveWedstrijd(Wedstrijd wedstrijd, Long id) throws DuplicateException, StadiumNotFoundException {
		Optional<Sport> sport = sportRepository.findById(id);

		if (sport.isEmpty()) {
			throw new EntityNotFound();
		}

		Optional<Stadium> stadium = stadiumRepository.findById(id);
		if (stadium.isEmpty()) {
			throw new StadiumNotFoundException("message.notfound.stadium");
		}

		wedstrijd.setStadium(stadium.get());
		wedstrijd.setSport(sport.get());
		wedstrijd.setId(null);

		// als 1(+ zou nie mogen) => aanwezig in db
		Long aantalInDb = wedstrijdRepository.countByOlympischNummer1(wedstrijd.getOlympischNummer1());
		
		if (aantalInDb >= 1) {
			throw new DuplicateException("message.save.fail.duplicate.sport");
		}

		wedstrijdRepository.save(wedstrijd);

	}

	@Override
	public List<Wedstrijd> getBySport(Long id) {
		Optional<Sport> sport = sportRepository.findById(id);
		if (sport.isEmpty())
			throw new EntityNotFound("message.notfound.sport");
		return wedstrijdRepository.findBySportOrderByDatumEnAanvangsuurAsc(sport.get());

		
	}

	@Override
	public Wedstrijd getWedstrijd(Long id) {

		Optional<Wedstrijd> wedstrijd = wedstrijdRepository.findById(id);

		if (wedstrijd.isEmpty()) {
			throw new EntityNotFound("message.notfound.wedstrijd");
		}

		return wedstrijd.get();
	}

	@Override
	public List<Stadium> getStadiums() {

		return (List<Stadium>) stadiumRepository.findAllByOrderByStadiumNaamAsc();
	}

	@Override
	public Long getSportIdOfWedstrijd(Long wedstrijdId) {
		return getWedstrijd(wedstrijdId).getSport().getId();

	}

	@Override
	public boolean wedstijdExistsById(Long wedstrijdId) {
		
		if(!wedstrijdRepository.existsById(wedstrijdId))
			throw new EntityNotFound("message.notfound.wedstrijd");
		
		return true;
	}

	@Override
	public Integer beschikbarePlaatsenVoorWedstrijd(Long wedstrijdId) {
		
		if(wedstijdExistsById(wedstrijdId)) {

			
			Integer aantal =  wedstrijdRepository.aantalBeschikbarePlaatsen(wedstrijdId);
	
			if(aantal != null) {
				return aantal;
			}
			
		
			
		}
		return 0;
		
	}
	
	

}
