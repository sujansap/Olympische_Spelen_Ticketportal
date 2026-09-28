package perform;


import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import domain.Wedstrijd;
import utility.PlaatsBeschikbaarhiedResponseRest;
import java.util.List;
import reactor.core.publisher.Mono;

public class PerformRestWedstrijd {

    private final String SERVER_URI = "http://localhost:8080/api"; 
    private WebClient webClient = WebClient.create();

    public PerformRestWedstrijd() {
        try {
            System.out.println("\n------- GET BESCHIKBARE PLAATSEN -------");
            getAvailablePlacesForWedstrijd(1L); 

            System.out.println("\n------- GET ALLE WEDSTRIJDEN -------");
            getAllWedstrijdenVanSport(1L); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAvailablePlacesForWedstrijd(Long wedstrijdId) {
        webClient.get().uri(SERVER_URI + "/plaatsen/{wedstrijdId}", wedstrijdId)
                .retrieve()
                .bodyToMono(PlaatsBeschikbaarhiedResponseRest.class)
                .doOnSuccess(response -> System.out.println("Beschikbare plaats: " + response.getAantalPlaatsen()))
                .block();
    }

    private void getAllWedstrijdenVanSport(Long sportId) {
        webClient.get().uri(SERVER_URI + "/wedstrijden/{sportId}", sportId)
                .retrieve()
                .bodyToFlux(Wedstrijd.class)
                .flatMap(wedstrijd -> {
                    printWedstrijdData(wedstrijd);
                    return Mono.empty();
                })
                .blockLast();
    }

    private void printWedstrijdData(Wedstrijd wedstrijd) {
        System.out.printf("ID=%s, Sport=%s, Stadium=%s, DatumEnAanvangsuur=%s, OlympischNummer1=%s, OlympischNummer2=%s, TicketPrijs=%s, Discipline1=%s, Discipline2=%s, AantalPlaatsen=%s%n",
                wedstrijd.getId(), wedstrijd.getSport(), wedstrijd.getStadium(), wedstrijd.getDatumEnAanvangsuur(),
                wedstrijd.getOlympischNummer1(), wedstrijd.getOlympischNummer2(), wedstrijd.getTicketPrijs(),
                wedstrijd.getDiscipline1(), wedstrijd.getDiscipline2(), wedstrijd.getAantalPlaatsen());
    }

    public static void main(String[] args) {
        new PerformRestWedstrijd();
    }
}
