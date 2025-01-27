package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class CompoundCapture extends Capture {
	private final int minMatch;
	private final int maxMatch;
	private final List<Capture> captures;
	private final List<List<Capture>> cap = new ArrayList<>();
	private final List<int[]> lens = new ArrayList<>();
	private final List<Integer> off = new ArrayList<>();
	private Capture endCapture;

	public CompoundCapture(Capture... cap) {
		this(1, cap);
	}

	public CompoundCapture(int mat, Capture... cap) {
		this(mat, mat, cap);
	}

	public CompoundCapture(int min, int max, Capture... cap) {
		minMatch = min;
		maxMatch = max;
		captures = Arrays.asList(cap);
	}

	public CompoundCapture setEndCapture(Capture end) {
		endCapture = end;
		return this;
	}

	@Override
	public int match(MatcherContext context, Tokenf token) throws TokenMatcher.MatchFailed {
		lens.clear();
		off.clear();
		cap.clear();

		boolean[] ended = new boolean[1];
		int of = 0;
		int max = context.getTokensCountInContext();
		for (int i = 0; i < maxMatch; i++) {
			if (token.getIndexInContext(context) + of >= max) {
				if (i < minMatch) throw TokenMatcher.MatchFailed.INSTANCE;
				else break;
			}

			Tokenf curr = context.getTokenInContext(token.getIndexInContext(context) + of);

			List<Capture> capt = new ArrayList<>();
			for (Capture c : captures) capt.add(c.create());
			cap.add(capt);

			int[] len = new int[captures.size()];
			try {
				ended[0] = false;
				int l = SerialMatcher.matchCapture(capt, endCapture, () -> ended[0] = true, len, context, curr);
				lens.add(len);
				off.add(of);
				if (ended[0] && endCapture != null && endCapture.matchOnly) break;
				of += l;
				if (ended[0]) break;
			} catch (TokenMatcher.MatchFailed e) {
				if (i < minMatch) throw e;
				else break;
			}
		}

		return of;
	}

	@Override
	public void applyScope(MatcherContext context, Tokenf token, int len) {
		int max = context.getTokensCountInContext();
		for (int i = 0; i < off.size(); i++) {
			if (token.getIndexInContext(context) + off.get(i) >= max) break;
			SerialMatcher.applyCapture(cap.get(i), lens.get(i), context,
					context.getTokenInContext(token.getIndexInContext(context) + off.get(i))
			);
		}
	}

	@Override
	public CompoundCapture create() {
		Capture[] cap = new Capture[captures.size()];
		for (int i = 0; i < cap.length; i++) cap[i] = captures.get(i).create();
		CompoundCapture capture = new CompoundCapture(minMatch, maxMatch, cap);
		capture.setMatchOnly(matchOnly).setOptional(optional);
		capture.endCapture = endCapture;
		return capture;
	}
}
