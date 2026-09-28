package service;

import java.util.List;


import domain.Sport;


public interface SportService {
	List<Sport> getSporten();
	void checkSport(Long id);
}
