package heavyindustry.graphics.g2d;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Texture;
import arc.math.Mathf;

import java.util.Arrays;

//MDTX: add some DebugUtil count.
//MDTX(WayZer): renderSort
public class MySpriteBatch extends MySpriteBatchBase {
	private static final int PRIME2 = 0xb4b82e39;
	int[] extraZ = new int[10000];
	//Add a small delta to maintain the original sequence
	int orderZ = 0;
	int hashZ = 0;//Shuffle hash values to check for rendering anomalies

	@Override
	protected void z(float z) {
		orderZ = 0;
		if (this.z == z) return;
		super.z(z);
	}

	@Override
	protected void flush() {
		super.flush();
	}

	@Override
	protected void flushRequests() {
		super.flushRequests();
	}

	@Override
	protected void switchTexture(Texture texture) {
		super.switchTexture(texture);
	}

	@Override
	protected void expandRequests() {
		super.expandRequests();
		extraZ = Arrays.copyOf(extraZ, requestZ.length);
	}

	@Override
	protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {
		super.draw(texture, spriteVertices, offset, count);
		if (sort && !flushing && Core.settings.getBool("hi-render-sort")) {
			int h = texture.hashCode() + hashZ;
			extraZ[numRequests - 1] = ((orderZ++) << 16) | (h & 0xfffc) | (blending == Blending.disabled ? 2 : 0) | (blending == Blending.additive ? 1 : 0);
		}
	}

	@Override
	protected void draw(Runnable request) {
		super.draw(request);
		if (sort && !flushing && Core.settings.getBool("hi-render-sort")) {
			int h = hashZ;
			extraZ[numRequests - 1] = ((orderZ++) << 16) | (h & 0xffff);
		}
	}

	@Override
	protected void sortRequests() {
		int numReq = numRequests;
		int[] arr = requestZ;
		if (Core.settings.getBool("hi-render-sort")) {
			hashZ = 0;
			int[] z1 = extraZ;
			// Map separately to narrow down the range and merge into one int range
			mapToRank(arr, numReq);
			mapToRank(z1, numReq);
			for (int i = 0; i < numReq; i++) {
				arr[i] = (arr[i] << 16) | z1[i];
			}
		}

		countingSortMap(arr, numReq);//arr is loc now;

		if (copy.length < requests.length) copy = new DrawRequest[requests.length];
		final DrawRequest[] items = requests, dest = copy;
		for (int i = 0; i < numReq; i++) {
			dest[arr[i]] = items[i];
		}
	}

	private static final IntIntMap vMap = new IntIntMap(10000, 0.25f);
	private static int[] orderArr = new int[1000], orderArr2 = new int[1000];

	/**
	 * Remap the input arr to the [0, unique) field and maintain the original value size relationship
	 *
	 * @param arr The array to be sorted will be mapped to rank as output, reflecting the size of the original values
	 * @return unique
	 */
	private static int mapToRank(int[] arr, int len) {
		var map = vMap;
		int[] order = orderArr;
		map.clear();
		int unique = 0;
		for (int i = 0; i < len; i++) {
			int v = arr[i];
			int id = map.getOrPut(v, unique);
			arr[i] = id;// Arr now represents ID
			if (id == unique) {
				if (order.length <= unique) {
					order = orderArr = Arrays.copyOf(order, unique << 1);
				}
				order[unique] = v;
				unique++;
			}
		}

		// Sort z values
		Arrays.sort(order, 0, unique);//order -> z

		// Store orders in arr
		int[] order2 = orderArr2;//id -> order
		if (order2.length < order.length) {
			order2 = orderArr2 = new int[order.length];
		}
		for (int i = 0; i < unique; i++) {
			order2[map.getOrPut(order[i], -1)] = i;
		}
		for (int i = 0; i < len; i++) {
			arr[i] = order2[arr[i]];
		}
		return unique;
	}

	/**
	 * Counting sort
	 *
	 * @param arr Array to be sorted, output as new loc
	 */
	private static void countingSortMap(int[] arr, int len) {
		int[] order = orderArr, counts = orderArr2;
		var map = vMap;//z->id
		map.clear();
		int unique = 0;
		for (int i = 0; i < len; i++) {
			int v = arr[i];
			int id = map.getOrPut(v, unique);
			arr[i] = id;// Arr now represents ID
			if (id == unique) {
				if (order.length <= unique) {
					order = orderArr = Arrays.copyOf(order, unique << 1);
					counts = orderArr2 = Arrays.copyOf(counts, unique << 1);
				}
				order[unique] = v;
				counts[unique] = 1;
				unique++;
			} else counts[id]++;
		}

		// Sort z values
		Arrays.sort(order, 0, unique);//order -> z

		// Convert counts to locs (starting position of each ID)
		for (int i = 0, loc = 0; i < unique; i++) {
			int id = map.getOrPut(order[i], -1);
			int c = counts[id];
			counts[id] = loc;
			loc += c;
		}
		// Arr now indicates a new destination
		for (int i = 0; i < len; i++) {
			arr[i] = counts[arr[i]]++;
		}
	}

	static public class IntIntMap {
		private int[] keys;
		private boolean hasZero;
		private int[] values;
		private int zeroValue;
		private int size; // The number of elements in the hash table

		private int capacity, maxSize;
		private float loadFactor;
		private int mask, hashShift;

		public IntIntMap(int capacity, float loadFactor) {
			setCapacity(capacity, loadFactor);
		}

		private int hash(int key) {
			key *= PRIME2;
			return (key ^ key >>> hashShift);
		}

		public int getOrPut(int key, int defaultValue) {
			if (key == 0) {
				if (hasZero) return zeroValue;
				zeroValue = defaultValue;
				hasZero = true;
				return defaultValue;
			}
			int mk = mask;
			int in = hash(key) & mk;
			int[] ks = keys;
			while (ks[in] != 0) {
				if (ks[in] == key) {// Key found
					return values[in];
				}
				in = (in + 1) & mk;
			}
			// The key does not exist
			ks[in] = key;
			values[in] = defaultValue;
			size++;
			if (size > maxSize) setCapacity(capacity << 1, loadFactor);
			return defaultValue;
		}

		private void setCapacity(int cap, float lf) {
			cap = Mathf.nextPowerOfTwo(cap);
			capacity = cap;
			loadFactor = lf;
			maxSize = (int) (cap * lf);
			int mk = mask = cap - 1;
			hashShift = 31 - Integer.numberOfTrailingZeros(cap);

			int[] oks = keys, oldValues = values;
			int[] ks = keys = new int[cap];
			int[] vs = values = new int[cap];
			if (oks == null || oldValues == null) return;
			for (int i = 0; i < oks.length; i++) {
				if (oks[i] == 0) continue;
				int index = hash(oks[i]) & mk;
				while (ks[index] != 0) {
					index = (index + 1) & mk;
				}
				ks[index] = oks[i];
				vs[index] = oldValues[i]; // Insert or update values
			}
		}

		private void clear() {
			Arrays.fill(keys, 0);
			Arrays.fill(values, 0);
			size = 0;
			hasZero = false;
		}
	}
}
