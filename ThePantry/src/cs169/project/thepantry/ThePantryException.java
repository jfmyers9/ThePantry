package cs169.project.thepantry;

public class ThePantryException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ThePantryException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThePantryException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public ThePantryException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public ThePantryException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}
	
	
	public void showErrorMessage() {
		// TODO displays error message to the user
		System.err.println(getMessage());
	}

}
