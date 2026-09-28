package domain;

import java.time.LocalDateTime;

public class Constante {
	public static final Integer TICKET_LIMIT_MIN=0;
    public static final Integer TICKET_LIMIT_MAX = 20;
    public static final Integer TICKET_LIMIT_MAX_TOTAAL=100;
	public static final Double MIN_TICKET_PRIJS = 0.00;
	public static final Double MAX_TICKET_PRIJS = 150.00;
	public static final Integer MIN_AANTAL_PLAATSEN = 0;
	public static final LocalDateTime BEGIN_TICKET_BOEKING_PERIODE = LocalDateTime.of(2024, 7, 26, 8, 0);
	public static final LocalDateTime EINDE_TICKET_BOEKING_PERIODE = LocalDateTime.of(2024, 8, 12, 0, 0); //tot 11 aug dus 12 aug mag niet
	public static final Integer START_UUR = 8;
	public static final Integer END_UUR = 0;
	
}

