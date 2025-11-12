package heavyindustry.util;

public class Failed extends Throwable {
	private static final long serialVersionUID = 4296195113032177543l;

	public Failed() {
		super();
	}

	public Failed(String message) {
		super(message);
	}

	public Failed(String message, Throwable cause) {
		super(message, cause);
	}

	public Failed(Throwable cause) {
		super(cause);
	}
}
