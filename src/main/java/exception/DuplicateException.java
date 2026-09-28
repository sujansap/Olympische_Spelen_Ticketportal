package exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DuplicateException extends Exception {

	@Autowired
	private MessageSource messageSource;
	
	private static final long serialVersionUID = 1L;
	@Getter private final String message;
	
	public DuplicateException()
	{
		message = "message.save.fail.duplicate";
	}
	
	public DuplicateException(String message)
	{
		this.message = message;
	}


	
}