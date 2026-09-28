package com.springBoot.olympischeSpelen;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springBoot.olympischeSpelen.WedstrijdRestController;
import domain.Wedstrijd;
import exception.EntityNotFound;
import service.WedstrijdService;
import utility.PlaatsBeschikbaarhiedResponseRest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WedstrijdRestControllerMockTest {

    @Mock
    private WedstrijdService wedstrijdService;

    @InjectMocks
    private WedstrijdRestController wedstrijdRestController;

    @Test
    public void testGetBeschikbarePlaatsenVoorWedstrijd() {
        Long wedstrijdId = 1L;
        Integer aantalPlaatsen = 100;
        PlaatsBeschikbaarhiedResponseRest expectedResponse = new PlaatsBeschikbaarhiedResponseRest(aantalPlaatsen);

        when(wedstrijdService.beschikbarePlaatsenVoorWedstrijd(wedstrijdId)).thenReturn(aantalPlaatsen);

        PlaatsBeschikbaarhiedResponseRest response = wedstrijdRestController.getBeschikbarePlaatsenVoorWedstrijd(wedstrijdId);

        assertEquals(expectedResponse.getAantalPlaatsen(), response.getAantalPlaatsen());
    }

    @Test
    public void testGetAlleWedstrijdenVanSport() {
       
        Long sportId = 1L;
        List<Wedstrijd> expectedWedstrijden = new ArrayList<>();
        expectedWedstrijden.add(new Wedstrijd());
        expectedWedstrijden.add(new Wedstrijd());
        
        when(wedstrijdService.getBySport(sportId)).thenReturn(expectedWedstrijden);

        List<Wedstrijd> wedstrijden = wedstrijdRestController.getAlleWedstrijdenVanSport(sportId);

        assertEquals(expectedWedstrijden, wedstrijden);
    }
    
    
    @Test
    public void testGetBeschikbarePlaatsenVoorWedstrijd_EntityNotFoundException() {
        Long wedstrijdId = 1L;
        
        when(wedstrijdService.beschikbarePlaatsenVoorWedstrijd(wedstrijdId)).thenThrow(new EntityNotFound());
        assertThrows(EntityNotFound.class, () -> wedstrijdRestController.getBeschikbarePlaatsenVoorWedstrijd(wedstrijdId));
    }

    @Test
    public void testGetAlleWedstrijdenVanSport_EntityNotFoundException() {        
        Long sportId = 1L;

        when(wedstrijdService.getBySport(sportId)).thenThrow(new EntityNotFound());
        assertThrows(EntityNotFound.class, () -> wedstrijdRestController.getAlleWedstrijdenVanSport(sportId));
    }
}
