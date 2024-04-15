package Kartoffel.Licht.Net;

public class CorruptPackageException extends RuntimeException{
	
	private static final long serialVersionUID = 789178612L;

	private String message;
	
	
	
	public CorruptPackageException(String message) {
		super();
		this.message = message;
	}



	@Override
	public String getMessage() {
		return message;
	}

}
