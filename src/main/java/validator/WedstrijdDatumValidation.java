package validator;

import java.time.LocalDateTime;
import java.util.Locale;

import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springBoot.olympischeSpelen.DateFormatter;

import domain.Constante;
import domain.Wedstrijd;



public class WedstrijdDatumValidation implements Validator {
	
	@Autowired
    private  DateFormatter dateFormatter;
	

	@Override
	public boolean supports(Class<?> klass) {
		return Wedstrijd.class.isAssignableFrom(klass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		System.out.print(" we are here");
		LocalDateTime dateTime = (LocalDateTime) target;
	

        String formattedBegin = dateFormatter.print(Constante.BEGIN_TICKET_BOEKING_PERIODE, Locale.getDefault());
        String formattedEinde = dateFormatter.print(Constante.EINDE_TICKET_BOEKING_PERIODE, Locale.getDefault());
        
	    
		if (dateTime == null || dateTime.isBefore(Constante.BEGIN_TICKET_BOEKING_PERIODE)
				|| dateTime.isAfter(Constante.EINDE_TICKET_BOEKING_PERIODE)||
				(dateTime.getHour() < Constante.START_UUR && dateTime.getHour() >= Constante.END_UUR)) {
			

			errors.rejectValue("datumEnAanvangsuur", "wedstrijd.datumEnAanvangsuur.invalidRange",new Object[]{formattedBegin , formattedEinde},
					"Datum en Aanvangsuur zijn niet geldig");

		}

	}
	

}
