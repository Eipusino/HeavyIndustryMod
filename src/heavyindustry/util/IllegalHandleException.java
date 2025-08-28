package heavyindustry.util;

public class IllegalHandleException extends RuntimeException {
	static final long serialVersionUID = -4306131767553148102l;

	public IllegalHandleException() {
		super();
	}

	public IllegalHandleException(String message) {
		super(message);
	}

	public IllegalHandleException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalHandleException(Throwable cause) {
		super(cause);
	}
}
