package repository;





import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


import domain.Stadium;

public interface StadiumRepository extends CrudRepository<Stadium, Long>
{
	Stadium findByStadiumNaam(String stadiumNaam);
	Optional<Stadium> findById(Long id);
	List<Stadium> findAllByOrderByStadiumNaamAsc();
	
}
