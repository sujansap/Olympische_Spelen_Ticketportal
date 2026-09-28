package validator;


import domain.Constante;
import domain.Wedstrijd;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OlympischeNummerValidation implements Validator {


    @Override
    public boolean supports(Class<?> klass) {
        return Wedstrijd.class.isAssignableFrom(klass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Wedstrijd wedstrijd = (Wedstrijd) target;

        validateOlympischeNummer1(wedstrijd.getOlympischNummer1(), errors);
        validateOlympicNummmer2(wedstrijd.getOlympischNummer1(), wedstrijd.getOlympischNummer2(), errors);
        validatePrijs(wedstrijd.getTicketPrijs(), errors);
        validateDiscipline(wedstrijd.getDiscipline1(),wedstrijd.getDiscipline2(), errors);
        validateAantalPlaatsen(wedstrijd.getAantalPlaatsen(), errors);
 
    }
    
    private void validateAantalPlaatsen(Integer aantalPlaatsen, Errors errors) {
		if(aantalPlaatsen==null) {
			return;
		}
    	if(aantalPlaatsen<=0) {
    		errors.rejectValue("aantalPlaatsen", "error.aantalPlaatsen.min2",  new Object[]{Constante.MIN_AANTAL_PLAATSEN},"Aantal plaatsne is lager dan minimum");
		}
		
	}

	private void validateDiscipline(String discipline1, String discipline2, Errors errors) {
		if(discipline1==null||discipline1.isBlank()||discipline1.isEmpty())
			return;
		if(discipline1.equals(discipline2)) {
			errors.rejectValue("discipline1", "error.discipline.zelfde",  new Object[]{Constante.MIN_TICKET_PRIJS},"Discipline1 en discipline2 mogen niet zelfde zijn");
		}
		
		
		
	}

	private void validatePrijs(Double ticketPrijs, Errors errors) {
        if (ticketPrijs == null) {
            errors.rejectValue("ticketPrijs", "error.ticketPrijs.null", "Ticketprijs mag niet null zijn");
            return;
        }
        
        if (ticketPrijs <= Constante.MIN_TICKET_PRIJS) {
            errors.rejectValue("ticketPrijs", "error.ticketPrijs.negative",  new Object[]{Constante.MIN_TICKET_PRIJS},"Ticketprijs mag niet kleiner zijn dan {0} euro");
        }
        
        if (ticketPrijs >= Constante.MAX_TICKET_PRIJS) {
            errors.rejectValue("ticketPrijs", "error.ticketPrijs.high",new Object[]{Constante.MAX_TICKET_PRIJS}, "Ticketprijs mag niet hoger zijn dan {0} euro");
        }
    }

	private void validateOlympischeNummer1(String olympicNumber1, Errors errors) {
        if (olympicNumber1 == null || olympicNumber1.isBlank() || olympicNumber1.isEmpty()) {
        	errors.rejectValue("olympischNummer1", "wedstrijd.olympischNummer1.notEmpty",
                    "Olympisch nummer mag niet leeg zijn");
        }
        else if(olympicNumber1.length() != 5 ) {
        	   errors.rejectValue("olympischNummer1", "wedstrijd.olympischNummer1.length",
                       "Olympisch nummer moet uit exact 5 cijfers bestaan.");
        }
        else if (olympicNumber1.startsWith("0")) {
            errors.rejectValue("olympischNummer1", "wedstrijd.olympischNummer1.startWithZero",
                    "Olympisch nummer mag niet met 0 beginnen.");
        } else if (olympicNumber1.charAt(0) == olympicNumber1.charAt(4)) {
            errors.rejectValue("olympischNummer1", "wedstrijd.olympischNummer1.firstAndLastDigits",
                    "Het eerste en laatste cijfer van het Olympisch nummer moeten verschillend zijn.");
        }
    }
    
    private void validateOlympicNummmer2(String olympicNumber1, String olympicNumber2, Errors errors) {
    	if (olympicNumber2 == null || olympicNumber2.isBlank() || olympicNumber2.isEmpty()) {
            errors.rejectValue("olympischNummer2", "wedstrijd.olympischNummer2.empty",
                    "Olympisch nummer 2 mag niet leeg zijn.");
            return;
        }
        
        try {
            int num1 = Integer.parseInt(olympicNumber1);
            int num2 = Integer.parseInt(olympicNumber2);
            
            if(num1==num2) {
                errors.rejectValue("olympischNummer1", "wedstrijd.olympischNummer1.verschillende",
                        "Olympisch nummer 1 en 2 moetne verschillende zijn");
            }
            if (num2 < num1 - 1000 || num2 > num1 + 1000) {
                errors.rejectValue("olympischNummer2", "wedstrijd.olympischNummer2.outOfRange",
                        "Olympisch nummer 2 moet liggen in een range van 1000 t.o.v. Olympisch nummer 1.");
            }
        } catch (NumberFormatException e) {
            errors.rejectValue("olympischNummer2", "wedstrijd.olympischNummer2.invalid",
                    "Olympisch nummer 2 moet een geldig getal zijn.");
        }
    }

}