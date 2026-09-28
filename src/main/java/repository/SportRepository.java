package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import domain.Sport;

public interface SportRepository extends CrudRepository<Sport, Long>
{
	Sport findBySportNaam(String sportNaam);
	boolean existsById(Long id);
	
}
