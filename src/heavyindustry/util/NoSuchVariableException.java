package heavyindustry.util;

public class NoSuchVariableException extends RuntimeException {
	private static final long serialVersionUID = -4605143723179677200l;

	public NoSuchVariableException() {
		super();
	}

	public NoSuchVariableException(String message) {
		super(message);
	}

	public NoSuchVariableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchVariableException(Throwable cause) {
		super(cause);
	}
}
