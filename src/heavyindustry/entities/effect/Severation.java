package heavyindustry.entities.effect;

import arc.Events;
import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.QuadTree;
import arc.math.geom.QuadTree.QuadTreeObject;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.content.HFx;
import heavyindustry.entities.HDamage.BasicPool;
import heavyindustry.entities.HEntity;
import heavyindustry.gen.BaseEntity;
import heavyindustry.gen.RenderGroupEntity;
import heavyindustry.gen.RenderGroupEntity.DrawnRegion;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

public class Severation extends BaseEntity implements QuadTreeObject {
	protected static FloatSeq intersections = new FloatSeq(), side1 = new FloatSeq(), side2 = new FloatSeq();
	protected static Seq<CutTri> returnTri = new Seq<>(CutTri.class), tmpTris = new Seq<>(CutTri.class), tmpTris2 = new Seq<>(CutTri.class);
	protected static Seq<Severation> tmpCuts = new Seq<>(Severation.class);
	protected static Vec2 tmpVec = new Vec2(), tmpVec2 = new Vec2(), tmpVec3 = new Vec2();
	protected static float[] tmpVerts = new float[24];
	protected static float minArea = 4f * 4f;

	public static int slashIds = 0;
	public static QuadTree<Severation> cutTree;
	public static Seq<Severation> cutsSeq = new Seq<>(Severation.class);
	public static Seq<Slash> slashes = new Seq<>(Slash.class);
	public static Pool<Slash> slashPool = new BasicPool<>(Slash::new);

	public Seq<CutTri> tris = new Seq<>(CutTri.class);
	public float bounds = 0f, area = 0f;
	public float centerX, centerY;
	public float rotation;
	public float width, height;
	public TextureRegion region = new TextureRegion();
	public IntSet collided = new IntSet();

	public float color = Color.whiteFloatBits;
	public float z = Layer.flyingUnit, shadowZ, zTime;
	public Effect explosionEffect = HFx.fragmentExplosion;
	public Sound explosionSound = Sounds.none;

	public float time = 0f, lifetime = 3f * 60f;
	public float vx, vy, vr;
	public float drag = 0.05f;
	public boolean wasCut = false;

	public static void init() {
		Events.on(ResetEvent.class, e -> {
			slashes.clear();
			cutsSeq.clear();
		});
		Events.on(WorldLoadEvent.class, e -> cutTree = new QuadTree<>(new Rect(-Vars.finalWorldBounds, -Vars.finalWorldBounds, Vars.world.width() * Vars.tilesize + Vars.finalWorldBounds * 2, Vars.world.height() * Vars.tilesize + Vars.finalWorldBounds * 2)));
	}

	public static void updateStatic() {
		if (Vars.state.isGame()) {
			if (cutTree != null) {
				cutTree.clear();
				for (Severation cuts : cutsSeq) {
					cutTree.insert(cuts);
				}
			}
			if (slashes.any()) {
				slashes.removeAll(s -> {
					HEntity.intersectLine(cutTree, 1f, s.x1, s.y1, s.x2, s.y2, (c, x, y) -> {
						Rect b = Tmp.r3;
						c.hitbox(b);
						if (!b.contains(s.x1, s.y1) && !b.contains(s.x2, s.y2) && c.collided.add(s.id)) {
							//float len = Mathf.random(0.8f, 1.2f);
							c.cutWorld(s.x1, s.y1, s.x2, s.y2, cc -> {
								Vec2 n = Intersector.nearestSegmentPoint(s.x1, s.y1, s.x2, s.y2, cc.x, cc.y, tmpVec3);
								int side = Intersector.pointLineSide(s.x1, s.y1, s.x2, s.y2, cc.x, cc.y);
								//float len = Mathf.rand
								//n.sub(cc.x, cc.y).nor().scl(-1.5f * len);
								float dst = n.dst(cc.x, cc.y);
								n.sub(cc.x, cc.y).scl((-2.5f) / 20f).limit(3f);
								cc.vx /= 1.5f;
								cc.vy /= 1.5f;
								cc.vr /= 1.5f;
								cc.vx += n.x;
								cc.vy += n.y;
								//cc.vr += -2f * side;
								cc.vr += (-5f * side) / (1f + dst / 5f);
							});
						}
					});
					s.time += Time.delta;
					if (s.time >= 4f) slashPool.free(s);
					return s.time >= 4f;
				});
			}
		}
	}

	public static void slash(float x1, float y1, float x2, float y2) {
		Slash s = slashPool.obtain();
		s.x1 = x1;
		s.y1 = y1;
		s.x2 = x2;
		s.y2 = y2;
		slashes.add(s);
	}

	public static Severation generate(TextureRegion region, float x, float y, float width, float height, float rotation) {
		Severation c = new Severation();
		c.region.set(region);
		//c.region = region;
		c.width = width;
		c.height = height;
		c.rotation = rotation;
		c.x = x;
		c.y = y;

		for (int i = 0; i < 2; i++) {
			CutTri tr = new CutTri();
			float[] p = tr.pos;
			p[0] = i == 0 ? 0f : 1f;
			p[1] = i == 0 ? 0f : 1f;
			p[2] = 1f;
			p[3] = 0f;
			p[4] = 0f;
			p[5] = 1f;

			c.tris.add(tr);
		}

		c.updateBounds();

		c.add();
		return c;
	}

	public void cutWorld(float x1, float y1, float x2, float y2, Cons<Severation> force) {
		if (!added || area < minArea) return;

		if (force == null) {
			float fx1 = x1, fy1 = y1, fx2 = x2, fy2 = y2;
			force = cc -> {
				Vec2 n = Intersector.nearestSegmentPoint(fx1, fy1, fx2, fy2, cc.x, cc.y, tmpVec2);
				int side = Intersector.pointLineSide(fx1, fy1, fx2, fy2, cc.x, cc.y);
				//float len = Mathf.rand
				//n.sub(cc.x, cc.y).nor().scl(-1.5f * len);
				float dst = n.dst(cc.x, cc.y);
				n.sub(cc.x, cc.y).scl((-2.5f) / 20f).limit(3f);
				cc.vx /= 1.5f;
				cc.vy /= 1.5f;
				cc.vr /= 1.5f;
				cc.vx += n.x;
				cc.vy += n.y;
				//cc.vr += -2f * side;
				cc.vr += (-5f * side) / (1f + dst / 5f);
			};
		}

		tmpVec.set(x1, y1).sub(x, y).rotate(-rotation);
		x1 = tmpVec.x / width + centerX;
		y1 = tmpVec.y / height + centerY;
		tmpVec.set(x2, y2).sub(x, y).rotate(-rotation);
		x2 = tmpVec.x / width + centerX;
		y2 = tmpVec.y / height + centerY;

		/*float cwx = centerX * width, cwy = centerY * height;
		tmpVec.set(x1, y1).sub(x + cwx, y + cwy).rotate(-rotation);
		x1 = tmpVec.x / width;
		y1 = tmpVec.y / height;
		tmpVec.set(x2, y2).sub(x + cwx, y + cwy).rotate(-rotation);
		x2 = tmpVec.x / width;
		y2 = tmpVec.y / height;*/


		/*Effect eff = Fx.hitBulletColor;
		Color col = Tmp.c1.rand();
		Vec2 v = unproject(x1, y1);
		eff.at(v.x, v.y, 0, col);
		unproject(x2, y2);
		eff.at(v.x, v.y, 0, col);
		col.rand();
		eff.at(ox, oy, 0, col);*/

		/*Tmp.v1.set(v.x, v.y).sub(x, y).rotate(-rotation);
		float vx = Tmp.v1.x / width + centerX;
		float vy = Tmp.v1.y / height + centerY;
		unproject(vx, vy);
		eff.at(v.x, v.y, 0, col.rand());*/

		Seq<Severation> s = cut(x1, y1, x2, y2);
		if (s.any()) {
			for (Severation c : s) {
				//if (c.area < 4f * 4f) continue;
				c.explosionEffect = explosionEffect;

				float dx = c.centerX - centerX;
				float dy = c.centerY - centerY;
				tmpVec.set(dx, dy).scl(width, height).rotate(rotation).add(x, y);

				c.rotation = rotation;
				c.x = tmpVec.x;
				c.y = tmpVec.y;
				c.vx += vx;
				c.vy += vy;

				force.get(c);

				c.add();
			}
			wasCut = true;
			remove();
		}
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public void update() {
		x += vx * Time.delta;
		y += vy * Time.delta;
		rotation += vr * Time.delta;

		zTime = Mathf.clamp(zTime + Time.delta / 40f);
		float drg = zTime < 1 ? drag : 0.2f;

		vx *= 1f - drg * Time.delta;
		vy *= 1f - drg * Time.delta;
		vr *= 1f - drg * Time.delta;

		if (time >= lifetime) {
			float b = Mathf.sqrt(area / 4f);
			explosionEffect.at(x, y, b);
			float shake = b / 3f;
			Effect.shake(shake, shake, x, y);
			//if (explosionSound != Sounds.none) explosionSound.at(x, y, 1f, Mathf.clamp(b / 1.1f));
			if (explosionSound != Sounds.none)
				explosionSound.at(x, y, Mathf.random(0.9f, 1.1f) * Math.max(1f / (1f + (b - 8f) / 70f), 0.5f), Mathf.clamp(b / 1.1f));

			remove();
		}
		float speed = area < minArea ? 2f : 1f;
		time += Time.delta * speed;
	}

	@Override
	public void hitbox(Rect out) {
		out.setCentered(x, y, bounds);
	}

	public Vec2 unproject(float x, float y) {
		return Tmp.v1.set((x - centerX) * width, (y - centerY) * height).rotate(rotation).add(this.x, this.y);
	}

	public void drawRender() {
		//return tmpVerts;
		//return null;
		float sin = Mathf.sinDeg(rotation);
		float cos = Mathf.cosDeg(rotation);
		float col = color, mcol = Color.clearFloatBits;
		TextureRegion r = region;

		for (CutTri t : tris) {
			float[] pos = t.pos, verts = tmpVerts;
			int vertI = 0;
			for (int i = 0; i < 8; i += 2) {
				int mi = Math.min(i, 4);

				float vx = (pos[mi] - centerX) * width;
				float vy = (pos[mi + 1] - centerY) * height;
				float tx = (vx * cos - vy * sin) + x;
				float ty = (vx * sin + vy * cos) + y;

				verts[vertI] = tx;
				verts[vertI + 1] = ty;
				verts[vertI + 2] = col;
				verts[vertI + 3] = Mathf.lerp(r.u, r.u2, pos[mi]);
				verts[vertI + 4] = Mathf.lerp(r.v2, r.v, pos[mi + 1]);
				verts[vertI + 5] = mcol;

				vertI += 6;
			}
			//Draw.z(trueZ);
			//Draw.vert(region.texture, verts, 0, 24);
			DrawnRegion reg = RenderGroupEntity.draw(Blending.normal, z, r.texture, verts, 0);
			reg.lifetime = 5f * 60f;
			reg.fadeCurveIn = 0.9f;
		}
	}

	@Override
	public void draw() {
		float sin = Mathf.sinDeg(rotation);
		float cos = Mathf.cosDeg(rotation);
		float col = color, mcol = Color.clearFloatBits;
		float oz = Draw.z();
		float sdz = shadowZ * (1 - zTime);

		TextureRegion r = region;
		Tmp.c1.set(Pal.shadow).a(Pal.shadow.a * Mathf.curve(zTime, 0f, 0.3f));
		float scol = Tmp.c1.toFloatBits();
		float trueZ = zTime < 1f ? z : (Layer.debris + 0.1f);

		//Draw.z(trueZ);
		for (CutTri t : tris) {
			float[] pos = t.pos, verts = tmpVerts;
			int vertI = 0;
			for (int i = 0; i < 8; i += 2) {
				int mi = Math.min(i, 4);

				float vx = (pos[mi] - centerX) * width;
				float vy = (pos[mi + 1] - centerY) * height;
				float tx = (vx * cos - vy * sin) + x;
				float ty = (vx * sin + vy * cos) + y;

				verts[vertI] = tx;
				verts[vertI + 1] = ty;
				verts[vertI + 2] = col;
				verts[vertI + 3] = Mathf.lerp(r.u, r.u2, pos[mi]);
				verts[vertI + 4] = Mathf.lerp(r.v2, r.v, pos[mi + 1]);
				verts[vertI + 5] = mcol;

				vertI += 6;
			}
			Draw.z(trueZ);
			Draw.vert(region.texture, verts, 0, 24);
			if (sdz > 0.001f) {
				Draw.z(Math.min(trueZ - 1f, Layer.darkness));
				//float scol = Pal.shadow.toFloatBits();
				//Tmp.c1
				for (int i = 0; i < 4; i++) {
					int j = i * 6;
					verts[j] += UnitType.shadowTX * sdz;
					verts[j + 1] += UnitType.shadowTY * sdz;
					verts[j + 2] = scol;
				}
				Draw.vert(region.texture, verts, 0, 24);
			}
		}
		/*Draw.color(Color.green);
		for (CutTri t : tris) {
			float[] pos = t.pos;
			Lines.beginLine();
			for (int i = 0; i < 6; i += 2) {
				//float x = pos[i], y = pos[i + 1];

				float vx = (pos[i] - centerX) * width;
				float vy = (pos[i + 1] - centerY) * height;
				float tx = (vx * cos - vy * sin) + x;
				float ty = (vx * sin + vy * cos) + y;

				Lines.linePoint(tx, ty);
			}
			Lines.endLine(true);
		}*/
		Draw.color();

		Draw.z(oz);
	}

	@Override
	public float clipSize() {
		return bounds * 2f;
	}

	public Seq<Severation> cut(float x1, float y1, float x2, float y2) {
		tmpTris.clear();
		tmpTris2.clear();
		tmpCuts.clear();
		boolean hasCut = false;
		for (CutTri t : tris) {
			Seq<CutTri> ts = t.cut(x1, y1, x2, y2);
			if (ts.any()) {
				hasCut = true;
			} else {
				tmpTris2.add(t);
			}
			tmpTris.addAll(ts);
		}
		if (hasCut) {
			Severation side1 = new Severation(), side2 = new Severation();
			//side1.region = side2.region = region;
			side1.region.set(region);
			side2.region.set(region);
			side1.z = side2.z = z;
			side1.shadowZ = side2.shadowZ = shadowZ;
			side1.width = side2.width = width;
			side1.height = side2.height = height;
			side1.time = side2.time = (time / 2);
			side1.zTime = side2.zTime = zTime;

			side1.collided.addAll(collided);
			side2.collided.addAll(collided);

			side1.lifetime = (3f * 60f) + Mathf.range(15f);
			side2.lifetime = (3f * 60f) + Mathf.range(15f);

			side1.color = side2.color = color;

			for (CutTri tr : tmpTris) {
				if (tr.side == 0) {
					side1.tris.add(tr);
				} else {
					side2.tris.add(tr);
				}
			}
			for (CutTri tri : tmpTris2) {
				float[] ps = tri.pos;
				int side = 0;
				for (int i = 0; i < 6; i += 2) {
					//mx += ps[i];
					//my += ps[i + 1];
					side += Intersector.pointLineSide(x1, y1, x2, y2, ps[i], ps[i + 1]);
				}

				//int side = pointLineSide(x1, y1, x2, y2, mx / 6f, my / 6f);
				if (side >= 0) {
					side1.tris.add(tri);
				} else {
					side2.tris.add(tri);
				}
			}
			side1.updateBounds();
			side2.updateBounds();
			tmpCuts.add(side1, side2);
		}
		return tmpCuts;
	}

	public void updateBounds() {
		float maxW = 0, minW = 1;
		float maxH = 0, minH = 1;
		float cx = 0, cy = 0;
		float tw = width, th = height;
		int cc = 0;
		area = 0f;
		for (CutTri t : tris) {
			for (int i = 0; i < t.pos.length; i += 2) {
				float x = t.pos[i], y = t.pos[i + 1];
				maxW = Math.max(x, maxW);
				minW = Math.min(x, minW);
				maxH = Math.max(y, maxH);
				minH = Math.min(y, minH);

				cx += x;
				cy += y;
				cc++;
			}
			float[] p = t.pos;
			area += Geometry.triangleArea(p[0] * tw, p[1] * th, p[2] * tw, p[3] * th, p[4] * tw, p[5] * th);
		}
		float w = maxW - minW, h = maxH - minH;

		centerX = cx / cc;
		centerY = cy / cc;
		//minBounds = Math.min(w * width, h * height);
		bounds = Math.max(w * Math.abs(width), h * Math.abs(height));
	}

	@Override
	public void add() {
		if (added) return;
		Groups.all.add(this);
		Groups.draw.add(this);
		cutsSeq.add(this);
		added = true;
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.all.remove(this);
		Groups.draw.remove(this);
		cutsSeq.remove(this);
		added = false;
	}

	public static class CutTri {
		//0-1
		public float[] pos = new float[3 * 2];
		public float area = 0f;
		//used for return
		public int side = 0;

		public Seq<CutTri> cut(float x1, float y1, float x2, float y2) {
			intersections.clear();
			side1.clear();
			side2.clear();
			returnTri.clear();

			for (int i = 0; i < 3; i++) {
				int i1 = i * 2;
				int i2 = ((i + 1) % 3) * 2;

				float lx1 = pos[i1], ly1 = pos[i1 + 1];
				float lx2 = pos[i2], ly2 = pos[i2 + 1];
				//intersectSegments
				if (Intersector.intersectSegments(lx1, ly1, lx2, ly2, x1, y1, x2, y2, tmpVec)) {
					//if (intersectSegments(lx1 * rsl, ly1 * rsl, lx2 * rsl, ly2 * rsl, x1 * rsl, y1 * rsl, x2 * rsl, y2 * rsl, tmpVec)) {
					intersections.add(tmpVec.x, tmpVec.y);
				}
			}
			//Log.info(intersections);
			if (intersections.size == 4) {
				int within = 0;
				for (int i = 0; i < 6; i += 2) {
					float sx = pos[i], sy = pos[i + 1];
					int side = Intersector.pointLineSide(x1, y1, x2, y2, sx, sy);
					if (side >= 0) {
						side1.add(sx, sy);
					} else {
						side2.add(sx, sy);
					}
					if (side == 0) within++;
				}
				if (side1.isEmpty() || side2.isEmpty() || within >= 2) return returnTri;

				for (int s = 0; s < 2; s++) {
					FloatSeq side = s == 0 ? side1 : side2;

					/*int id1 = side.size - 2;
					float ix = side.items[id1];
					float iy = side.items[id1 + 1];
					float dst1 = 0f, dst2 = 0f;

					for (int i = 0; i < intersections.size; i += 2) {
						float dx = intersections.items[i] - ix, dy = intersections.items[i + 1] - iy;
						float dst = dx * dx + dy * dy;

						if (i == 0) {
							dst1 = dst;
						} else {
							dst2 = dst;
						}
					}
					if (dst1 < dst2) {
						side.add(intersections.items[0], intersections.items[1]);
						side.add(intersections.items[2], intersections.items[3]);
					} else {
						side.add(intersections.items[2], intersections.items[3]);
						side.add(intersections.items[0], intersections.items[1]);
					}*/
					if (side.size <= 2) {
						side.add(intersections.items[0], intersections.items[1]);
						side.add(intersections.items[2], intersections.items[3]);
					} else {
						int lp = side.size - 2;
						float fx = side.items[0], fy = side.items[1];
						float lx = side.items[lp], ly = side.items[lp + 1];
						int bias = 0;

						/*for (int i = 0; i < 4; i += 2) {
							float dx = intersections.items[i], dy = intersections.items[i + 1];
							//intersectSegments(lx1, ly1, lx2, ly2, x1, y1, x2, y2, tmpVec)

							for (int j = 0; j < 4; j += 2) {
								float dx2 = intersections.items[j], dy2 = intersections.items[j + 1];
								if ((dx == dx2 && dy == dy2) || !intersectSegments(fx, fy, dx, dy, lx, ly, dx2, dy2)) {
								//if ((dx != dx2 || dy != dy2) && !intersectSegments(fx, fy, dx, dy, lx, ly, dx2, dy2)) {
									bias += (j == 0 ? 1 : -1);
								}
							}
						}*/
						float px1 = intersections.items[0], py1 = intersections.items[1];
						float px2 = intersections.items[2], py2 = intersections.items[3];
						for (int i = 0; i < 4; i += 2) {
							float dx = intersections.items[i], dy = intersections.items[i + 1];

							boolean intersect1 = Intersector.intersectSegments(lx, ly, dx, dy, fx, fy, px1, py1, null);
							boolean intersect2 = Intersector.intersectSegments(lx, ly, dx, dy, fx, fy, px2, py2, null);

							if ((dx != px1 || dy != py1) && intersect1) {
								bias++;
							}
							if ((dx != px2 || dy != py2) && intersect2) {
								bias--;
							}
						}
						if (bias >= 0) {
							side.add(intersections.items[0], intersections.items[1]);
							side.add(intersections.items[2], intersections.items[3]);
						} else {
							side.add(intersections.items[2], intersections.items[3]);
							side.add(intersections.items[0], intersections.items[1]);
						}
					}

					if (side.size <= 6) {
						float[] items = side.items;
						float area = Geometry.triangleArea(items[0], items[1], items[2], items[3], items[4], items[5]);

						if (area > 0f) {
							CutTri c = new CutTri();
							c.side = s;
							c.area = area;
							System.arraycopy(side.items, 0, c.pos, 0, 6);
							returnTri.add(c);
						}
					} else {
						float[][] tr = triangulate(side.items, side.size / 2);
						for (float[] ps : tr) {
							float area = Geometry.triangleArea(ps[0], ps[1], ps[2], ps[3], ps[4], ps[5]);

							if (area > 0f) {
								CutTri c = new CutTri();
								c.side = s;
								c.area = area;
								System.arraycopy(ps, 0, c.pos, 0, 6);
								returnTri.add(c);
							}
						}
					}
				}
			}
			return returnTri;
		}

		public float[][] triangulate(float[] arr, int size) {
			float[][] ret = new float[size - 2][3 * 2];
			for (int i = 0; i < size - 2; i++) {
				float[] ar = ret[i];
				int id = i * 2;
				float sx = arr[0], sy = arr[1];
				float px1 = arr[id + 2], py1 = arr[id + 3];
				float px2 = arr[id + 4], py2 = arr[id + 5];
				ar[0] = sx;
				ar[1] = sy;
				ar[2] = px1;
				ar[3] = py1;
				ar[4] = px2;
				ar[5] = py2;
			}
			return ret;
		}
	}

	public static class Slash implements Poolable {
		public float x1, y1, x2, y2;
		public float time;
		public int id = slashIds++;

		@Override
		public void reset() {
			x1 = y1 = x2 = y2 = 0f;
			time = 0f;
			id = slashIds++;
		}
	}
}
