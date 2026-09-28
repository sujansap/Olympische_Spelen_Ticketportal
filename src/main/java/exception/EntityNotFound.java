package exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class EntityNotFound extends RuntimeException {

	@Autowired
	private MessageSource messageSource;
	
	private static final long serialVersionUID = 1L;
	@Getter private final String message;
	
	public EntityNotFound()
	{
		message = "message.notfound";
	}

	public EntityNotFound(String message) {
		this.message = message;
	}
}