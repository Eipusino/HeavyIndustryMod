package heavyindustry.util;

public class DisjointSetUnion {
	public final int size;

	final int[] father;
	final int[] sizes;

	public DisjointSetUnion(int v) {
		size = v;
		father = new int[v];
		sizes = new int[v];
		for (int i = 0; i < v; ++i) {
			father[i] = i;
			sizes[i] = 1;
		}
	}

	public int find(int id) {
		return father[id] == id ? id : (father[id] = find(father[id]));
	}

	public void unite(int v1, int v2) {
		int x = find(v1), y = find(v2);
		if (v1 == v2) return;
		if (sizes[x] < sizes[y]) {
			int tmp = x;
			x = y;
			y = tmp;
		}
		father[y] = x;
		sizes[x] += sizes[y];
	}

	public int sizeOf(int id) {
		return sizes[id];
	}

	public boolean erase(int id) {
		if (sizes[id] > 1) return false;
		sizes[find(id)]--;
		father[id] = id;
		return true;
	}

	public void move(int v1, int v2) {
		int x = find(v1), y = find(v2);
		if (x == y) return;
		father[v1] = y;
		sizes[x]--;
		sizes[y]++;
	}

	public boolean alone(int id) {
		return find(id) == id;
	}
}
