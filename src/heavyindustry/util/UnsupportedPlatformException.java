package heavyindustry.util;

public class UnsupportedPlatformException extends RuntimeException {
	static final long serialVersionUID = -3486987861831267136l;

	public UnsupportedPlatformException() {
		super();
	}

	public UnsupportedPlatformException(String message) {
		super(message);
	}

	public UnsupportedPlatformException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedPlatformException(Throwable cause) {
		super(cause);
	}
}
