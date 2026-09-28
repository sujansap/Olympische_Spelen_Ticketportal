package repository;

import java.util.List;



import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import domain.Sport;
import domain.Wedstrijd;

public interface WedstrijdRepository extends CrudRepository<Wedstrijd, Long>{
	List<Wedstrijd> findBySportOrderByDatumEnAanvangsuurAsc(Sport sport);
	Integer aantalBeschikbarePlaatsen(@Param("wedstrijdId")Long wedstrijdId);
	Wedstrijd findByOlympischNummer1(String olympischNummer1);
	Long countByOlympischNummer1(String olympischNummer1 );
	boolean existsById(Long id);
}
