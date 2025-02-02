package heavyindustry.ui.elements.markdown.highlighter;

import arc.func.*;

public class LazyCapture extends Capture {
	private final Prov<Capture> captureProv;

	private Capture capture;

	public LazyCapture(Prov<Capture> captureProv) {
		this.captureProv = captureProv;
	}

	@Override
	public int match(MatcherContext context, Tokenf token) throws TokenMatcher.MatchFailed {
		if (capture == null) capture = captureProv.get();

		return capture.match(context, token);
	}

	@Override
	public void applyScope(MatcherContext context, Tokenf token, int matchedLen) {
		if (capture == null) capture = captureProv.get();

		capture.applyScope(context, token, matchedLen);
	}

	@Override
	public Capture create() {
		LazyCapture capture = new LazyCapture(captureProv);
		capture.setMatchOnly(matchOnly).setOptional(optional);
		return capture;
	}
}
