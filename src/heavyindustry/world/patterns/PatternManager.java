package heavyindustry.world.patterns;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.math.geom.Point2;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.util.Threads;
import heavyindustry.type.shape.Shape;
import heavyindustry.util.CollectionList;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.CollectionObjectSet;
import heavyindustry.util.IntMap2;
import mindustry.Vars;
import mindustry.game.EventType.TileOverlayChangeEvent;
import mindustry.world.Block;
import mindustry.world.Tile;
import org.jetbrains.annotations.Nullable;

public final class PatternManager {
	static final CollectionObjectMap<Tile, PatternAnchor> anchorMap = new CollectionObjectMap<>(Tile.class, PatternAnchor.class);
	static final IntMap2<CollectionObjectMap<Block, Tile>> tileToAnchorMap = new IntMap2<>(CollectionObjectMap.class);

	static final CollectionList<Tile> dirtyTiles = new CollectionList<>(Tile.class);
	static QuadTree<PatternAnchor> anchorTree;
	static @Nullable Thread runningThread;

	private PatternManager() {}

	public static void init() {
		Events.on(TileOverlayChangeEvent.class, event -> {
			if (event.overlay instanceof Patterned || event.previous instanceof Patterned) {
				updateAround(event.tile);
				for (int i = 0; i < 4; i++) {
					Tile near = event.tile.nearby(i);
					if (near != null) {
						updateAround(near);
					}
				}
			}
		});
		anchorTree = new QuadTree<>(new Rect(0, 0, Vars.world.unitWidth(), Vars.world.unitHeight()));
		anchorMap.clear();
		tileToAnchorMap.clear();
		dirtyTiles.clear();
		IntSet allTiles = new IntSet();
		for (int y = 0; y < Vars.world.height(); y++) {
			for (int x = 0; x < Vars.world.width(); x++) {
				allTiles.add(Point2.pack(x, y));
			}
		}
		resolveTiles(allTiles, new IntMap2<>(CollectionObjectSet.class));
	}

	public static void updateAround(Tile tile) {
		if (tile == null || Vars.world.isGenerating()) return;
		if (!dirtyTiles.contains(tile)) dirtyTiles.add(tile);
		Core.app.post(PatternManager::processDirtyTiles);
	}

	static void processDirtyTiles() {
		if (anchorTree == null) {
			init();
		}
		if (dirtyTiles.isEmpty()) return;
		if (runningThread != null && runningThread.isAlive()) {
			return;
		}

		final CollectionList<Tile> dirty = new CollectionList<>(dirtyTiles);
		dirtyTiles.clear();

		final IntMap2<CollectionObjectMap<Block, Tile>> tileToAnchorMapCopy = new IntMap2<>(CollectionObjectMap.class);
		for (var entry : tileToAnchorMap.entries()) {
			tileToAnchorMapCopy.put(entry.key, new CollectionObjectMap<>(entry.value));
		}
		final CollectionObjectMap<Tile, PatternAnchor> anchorMapCopy = new CollectionObjectMap<>(anchorMap);

		runningThread = Threads.thread("Pattern-Resolver", () -> {
			IntMap2<CollectionObjectSet<Block>> visited = new IntMap2<>(CollectionObjectSet.class);
			IntSet toResolve = new IntSet();
			CollectionObjectSet<PatternAnchor> toRemove = new CollectionObjectSet<>(PatternAnchor.class);

			for (Tile tile : dirty) {
				for (Patterned p : getPatternedBlocks(tile)) {
					if (!(p instanceof Block pBlock)) continue;

					CollectionObjectSet<Block> visitedBlocks = visited.get(tile.pos());
					if (visitedBlocks != null && visitedBlocks.contains(pBlock)) continue;

					IntSet contiguous = findContiguousTiles(tile, p, visited);
					toResolve.addAll(contiguous);

					contiguous.each(pos -> {
						CollectionObjectMap<Block, Tile> map = tileToAnchorMapCopy.get(pos);
						if (map != null) {
							Tile anchorTile = map.get(pBlock);
							if (anchorTile != null) {
								PatternAnchor anchor = anchorMapCopy.get(anchorTile);
								if (anchor != null) toRemove.add(anchor);
							}
						}
					});
				}
			}

			toRemove.each(anchor -> {
				anchor.shape.each((x, y) -> {
					if (anchor.shape.get(x, y)) {
						Tile member = Vars.world.tile(anchor.tile.x + x, anchor.tile.y + y);
						if (member != null) {
							toResolve.add(member.pos());
						}
					}
				});
			});

			final CollectionList<PatternAnchor> anchorsToAdd = new CollectionList<>(PatternAnchor.class);
			if (!toResolve.isEmpty()) {
				resolveTilesAsync(toResolve, anchorsToAdd);
			}

			Core.app.post(() -> {
				toRemove.each(anchor -> {
					anchorTree.remove(anchor);
					anchorMap.remove(anchor.tile);
					anchor.shape.each((x, y) -> {
						if (anchor.shape.get(x, y)) {
							Tile member = Vars.world.tile(anchor.tile.x + x, anchor.tile.y + y);
							if (member != null) {
								CollectionObjectMap<Block, Tile> map = tileToAnchorMap.get(member.pos());
								if (map != null) {
									map.remove((Block) anchor.patterned);
									if (map.isEmpty()) {
										tileToAnchorMap.remove(member.pos());
									}
								}
							}
						}
					});
				});

				for (PatternAnchor pa : anchorsToAdd) {
					addAnchor(pa.patterned, pa.tile, new IntMap2<>(CollectionObjectSet.class));
				}

				toResolve.each(pos -> {
					Tile t = Vars.world.tile(pos);
					if (t != null) Vars.renderer.blocks.floor.recacheTile(t);
				});

				if (dirtyTiles.any()) {
					Core.app.post(PatternManager::processDirtyTiles);
				}
			});
		});
	}

	static void resolve(IntSet toResolve, IntMap2<CollectionObjectSet<Block>> resolved, Cons<PatternAnchor> onPatternFound) {
		if (toResolve.isEmpty()) return;

		IntMap2<CollectionObjectSet<Block>> processedAnchors = new IntMap2<>(CollectionObjectSet.class);

		IntSeq sortedToResolve = new IntSeq(toResolve.size);
		IntSet.IntSetIterator it = toResolve.iterator();
		while (it.hasNext) {
			sortedToResolve.add(it.next());
		}
		sortedToResolve.sort();

		sortedToResolve.each(pos -> {
			CollectionObjectSet<Block> resolvedBlocks = resolved.get(pos);

			Tile tile = Vars.world.tile(pos);
			if (tile == null) return;

			for (Patterned p : getPatternedBlocks(tile)) {
				if (!(p instanceof Block pBlock)) continue;
				if (resolvedBlocks != null && resolvedBlocks.contains(pBlock)) continue;

				Shape shape = p.getShape();
				shape.each((sx, sy) -> {
					if (shape.get(sx, sy)) {
						Tile potentialAnchor = Vars.world.tile(tile.x - sx, tile.y - sy);
						if (potentialAnchor != null) {
							CollectionObjectSet<Block> checked = processedAnchors.get(potentialAnchor.pos());
							if (checked != null && checked.contains(pBlock)) return;

							if (checked == null) {
								checked = new CollectionObjectSet<>(Block.class);
								processedAnchors.put(potentialAnchor.pos(), checked);
							}
							checked.add(pBlock);

							if (isPatternComplete(p, potentialAnchor, resolved)) {
								onPatternFound.get(new PatternAnchor(potentialAnchor, p));
							}
						}
					}
				});
			}
		});
	}

	static void resolveTilesAsync(IntSet toResolve, CollectionList<PatternAnchor> anchorsToAdd) {
		IntMap2<CollectionObjectSet<Block>> resolved = new IntMap2<>(CollectionObjectSet.class);
		resolve(toResolve, resolved, anchor -> {
			anchorsToAdd.add(anchor);
			anchor.patterned.getShape().each((ssx, ssy) -> {
				if (anchor.patterned.getShape().get(ssx, ssy)) {
					Tile member = Vars.world.tile(anchor.tile.x + ssx, anchor.tile.y + ssy);
					if (member != null) {
						CollectionObjectSet<Block> resolvedSet = resolved.get(member.pos());
						if (resolvedSet == null) {
							resolvedSet = new CollectionObjectSet<>(Block.class);
							resolved.put(member.pos(), resolvedSet);
						}
						resolvedSet.add((Block) anchor.patterned);
					}
				}
			});
		});
	}

	static void resolveTiles(IntSet toResolve, IntMap2<CollectionObjectSet<Block>> resolved) {
		resolve(toResolve, resolved, anchor -> addAnchor(anchor.patterned, anchor.tile, resolved));
	}

	static IntSet findContiguousTiles(Tile startTile, Patterned patterned, IntMap2<CollectionObjectSet<Block>> visited) {
		if (!(patterned instanceof Block pBlock)) return new IntSet();

		if (isVisited(visited, startTile.pos(), pBlock)) return new IntSet();

		IntSet contiguous = new IntSet();
		IntSeq stack = new IntSeq();

		stack.add(startTile.pos());

		int width = Vars.world.width(), height = Vars.world.height();

		while (stack.size > 0) {
			int popped = stack.pop();
			int startX = Point2.x(popped);
			int y = Point2.y(popped);

			int x1 = startX;
			while (x1 >= 0 && hasPatterned(Vars.world.tile(x1, y), patterned) && !isVisited(visited, Point2.pack(x1, y), pBlock)) {
				x1--;
			}
			x1++;

			boolean spanAbove = false;
			boolean spanBelow = false;

			while (x1 < width && hasPatterned(Vars.world.tile(x1, y), patterned) && !isVisited(visited, Point2.pack(x1, y), pBlock)) {
				visit(visited, x1, y, pBlock);
				contiguous.add(Point2.pack(x1, y));

				if (!spanAbove && y > 0 && hasPatterned(Vars.world.tile(x1, y - 1), patterned) && !isVisited(visited, Point2.pack(x1, y - 1), pBlock)) {
					stack.add(Point2.pack(x1, y - 1));
					spanAbove = true;
				} else if (spanAbove && !(hasPatterned(Vars.world.tile(x1, y - 1), patterned) && !isVisited(visited, Point2.pack(x1, y - 1), pBlock))) {
					spanAbove = false;
				}

				if (!spanBelow && y < height - 1 && hasPatterned(Vars.world.tile(x1, y + 1), patterned) && !isVisited(visited, Point2.pack(x1, y + 1), pBlock)) {
					stack.add(Point2.pack(x1, y + 1));
					spanBelow = true;
				} else if (spanBelow && y < height - 1 && !(hasPatterned(Vars.world.tile(x1, y + 1), patterned) && !isVisited(visited, Point2.pack(x1, y + 1), pBlock))) {
					spanBelow = false;
				}
				x1++;
			}
		}

		return contiguous;
	}

	static boolean isVisited(IntMap2<CollectionObjectSet<Block>> visited, int pos, Block pBlock) {
		CollectionObjectSet<Block> set = visited.get(pos);
		return set != null && set.contains(pBlock);
	}

	static void visit(IntMap2<CollectionObjectSet<Block>> visited, int x, int y, Block pBlock) {
		int pos = Point2.pack(x, y);
		CollectionObjectSet<Block> set = visited.get(pos);
		if (set == null) {
			set = new CollectionObjectSet<>(Block.class);
			visited.put(pos, set);
		}
		set.add(pBlock);
	}

	static void addAnchor(Patterned p, Tile anchor, IntMap2<CollectionObjectSet<Block>> localClaimed) {
		if (!(p instanceof Block pBlock)) return;

		PatternAnchor pa = new PatternAnchor(anchor, p);
		anchorTree.insert(pa);
		anchorMap.put(anchor, pa);

		Shape shape = p.getShape();
		shape.each((x, y) -> {
			if (shape.get(x, y)) {
				Tile member = Vars.world.tile(anchor.x + x, anchor.y + y);
				if (member != null) {
					CollectionObjectMap<Block, Tile> map = tileToAnchorMap.get(member.pos());
					if (map == null) {
						map = new CollectionObjectMap<>(Block.class, Tile.class);
						tileToAnchorMap.put(member.pos(), map);
					}
					map.put(pBlock, anchor);

					CollectionObjectSet<Block> claimedSet = localClaimed.get(member.pos());
					if (claimedSet == null) {
						claimedSet = new CollectionObjectSet<>(Block.class);
						localClaimed.put(member.pos(), claimedSet);
					}
					claimedSet.add(pBlock);
				}
			}
		});
	}

	static boolean isPatternComplete(Patterned patterned, Tile anchor, IntMap2<CollectionObjectSet<Block>> localClaimed) {
		if (!(patterned instanceof Block pBlock)) return false;

		for (int x = 0; x < patterned.getShape().width(); x++) {
			for (int y = 0; y < patterned.getShape().height(); y++) {
				if (patterned.getShape().get(x, y)) {
					Tile other = Vars.world.tile(anchor.x + x, anchor.y + y);
					if (!hasPatterned(other, patterned)) {
						return false;
					}
					if (localClaimed != null) {
						CollectionObjectSet<Block> claimed = localClaimed.get(other.pos());
						if (claimed != null && claimed.contains(pBlock)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean isPatternComplete(Patterned patterned, Tile anchor) {
		return isPatternComplete(patterned, anchor, null);
	}

	public static Tile getAnchor(Tile tile, Patterned p) {
		if (tile == null || !(p instanceof Block pBlock)) return null;
		CollectionObjectMap<Block, Tile> map = tileToAnchorMap.get(tile.pos());
		if (map == null) return null;
		return map.get(pBlock);
	}

	public static CollectionList<Patterned> getPatternedBlocks(Tile tile) {
		CollectionList<Patterned> result = new CollectionList<>(3, Patterned.class);
		if (tile == null) return result;

		if (tile.block() instanceof Patterned p) result.add(p);
		if (tile.floor() instanceof Patterned p) result.add(p);
		if (tile.overlay() instanceof Patterned p) result.add(p);
		return result;
	}

	public static boolean hasPatterned(Tile tile, Patterned p) {
		if (tile == null || !(p instanceof Block pBlock)) return false;
		return tile.block() == pBlock || tile.floor() == pBlock || tile.overlay() == pBlock;
	}

	static class PatternAnchor implements QuadTree.QuadTreeObject {
		public final Tile tile;
		public final Patterned patterned;
		public final Shape shape;
		public final Rect bounds = new Rect();

		public PatternAnchor(Tile tile, Patterned patterned) {
			this.tile = tile;
			this.patterned = patterned;
			this.shape = patterned.getShape();
			this.bounds.set(tile.x, tile.y, shape.width(), shape.height());
		}

		@Override
		public void hitbox(Rect rect) {
			rect.set(bounds);
		}
	}
}
