package heavyindustry.gen;

import arc.func.Cons;
import mindustry.gen.Unit;

public interface Chainedc extends BaseUnitc {
	<T extends Unit & Chainedc> int countBackwards();

	<T extends Unit & Chainedc> int countForward();

	<T extends Unit & Chainedc> void consBackwards(Cons<T> var1);

	<T extends Unit & Chainedc> void consForward(Cons<T> var1);

	<T extends Unit & Chainedc> void grow();

	boolean grown();

	boolean isHead();

	boolean isSegment();

	boolean isTail();

	float chainTime();

	float growTime();

	Unit child();

	Unit head();

	Unit parent();

	Unit tail();

	void chainTime(float value);

	void child(Unit value);

	void connect(Unit value);

	void growTime(float value);

	void grown(boolean value);

	void head(Unit value);

	void parent(Unit value);

	void tail(Unit value);
}
