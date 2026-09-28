package service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import domain.Sport;
import exception.EntityNotFound;
import repository.SportRepository;

public class SportServiceImpl implements SportService {
	@Autowired
	private SportRepository sportRepository;
	
	
	public List<Sport> getSporten(){
		
		List<Sport> sporten = (List<Sport>) sportRepository.findAll();
		return sporten;
		
	}


	@Override
	public void checkSport(Long id) {
		 boolean erIsSportMetId = sportRepository.existsById(id);
		 
		 if(!erIsSportMetId) {
			 throw new EntityNotFound("message.notfound.sport");
		 }
		 
		 
	}
}
