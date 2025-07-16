package heavyindustry.type;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Cubemap;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Reflect;
import heavyindustry.HVars;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.gl.DepthFrameBuffer;
import heavyindustry.graphics.gl.DepthFrameBufferCubemap;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.CubemapMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class BlackHole extends Planet {
	private static final Mat3D mat = new Mat3D();
	private static final Vec3 v1 = new Vec3(), v2 = new Vec3(), v3 = new Vec3();
	private static final Camera3D frustum = new Camera3D();

	public float horizon = 0.4f;

	public @Nullable Cubemap skybox;
	public @Nullable DepthFrameBufferCubemap pov;
	public @Nullable DepthFrameBuffer orbit;

	protected Seq<Planet> stashChildren = new Seq<>(), requests = new Seq<>();
	protected Cubemap stashSkybox;
	protected boolean drawing = false;

	public BlackHole(String name, float radius) {
		super(name, null, radius, 0);

		Events.run(Trigger.universeDrawBegin, () -> {
			if (Vars.ui.planet.state.planet.solarSystem == this) {
				stashSkybox = Reflect.get(CubemapMesh.class, Vars.renderer.planets.skybox, "map");
				Vars.renderer.planets.skybox.setCubemap(skybox);
			}
		});

		Events.run(Trigger.universeDrawEnd, () -> {
			if (stashSkybox != null) {
				Vars.renderer.planets.skybox.setCubemap(stashSkybox);
				stashSkybox = null;
			}

			if (!drawing) return;
			drawing = false;

			var stash = children;
			children = stashChildren;
			stashChildren = stash;
		});
	}

	@Override
	public void load() {
		super.load();
		if (!Vars.headless) {
			if (skybox == null) {
				skybox = new Cubemap(
						megalith("right.png"),
						megalith("left.png"),
						megalith("top.png"),
						megalith("bottom.png"),
						megalith("front.png"),
						megalith("back.png")
				);
			}

			if (pov == null) {
				pov = new DepthFrameBufferCubemap(2, 2, true);
				pov.getTexture().setFilter(TextureFilter.nearest);
			}

			if (orbit == null) {
				orbit = new DepthFrameBuffer(2, 2, true);
				orbit.getTexture().setFilter(TextureFilter.nearest);
			}
		}
	}

	protected Fi megalith(String name) {
		return HVars.internalTree.child("sprites/megalith/" + name);
	}

	@Override
	public void draw(PlanetParams params, Mat3D projection, Mat3D transform) {
		if (Vars.mobile) return;

		// Fool `PlanetRenderer` into thinking the black hole has no children, so that it can draw them itself.
		var stash = stashChildren;
		stashChildren = children;
		children = stash;

		drawing = true;
		var cam = Vars.renderer.planets.cam;

		float fov = cam.fov, w = cam.width, h = cam.height;

		var up = v1.set(cam.up);
		var dir = v2.set(cam.direction);

		var shader = HShaders.blackHole;
		HVars.sizedGraphics.override(Math.max(Core.graphics.getWidth(), Core.graphics.getHeight()), () -> {
			cam.fov = 90f;
			cam.width = Core.graphics.getWidth();
			cam.height = Core.graphics.getHeight();

			frustum.position.set(position);
			frustum.fov = 30f;
			frustum.width = frustum.height = 1f;
			frustum.lookAt(cam.position);
			frustum.update();

			requests.clear();
			visit(this, requests::add);

			pov.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			pov.begin();
			pov.eachSide(side -> {
				Gl.clearColor(0f, 0f, 0f, 0f);
				Gl.clear(Gl.colorBufferBit | Gl.depthBufferBit);

				side.getUp(cam.up);
				side.getDirection(cam.direction);

				if (params.drawSkybox) {
					var lastPos = v3.set(cam.position);
					cam.position.setZero();
					cam.update();

					Gl.depthMask(false);
					Vars.renderer.planets.skybox.render(cam.combined);
					Gl.depthMask(true);

					cam.position.set(lastPos);
				}

				cam.update();
				shader.cubemapView[side.index].set(cam.view);

				for (Planet p : requests) {
					if (!p.visible()) continue;
					if (cam.frustum.containsSphere(p.position, p.clipRadius) && !frustum.frustum.containsSphere(p.position, p.clipRadius)) {
						p.draw(params, cam.combined, p.getTransform(mat));
					}
				}

				for (Planet p : requests) {
					if (!p.visible()) continue;
					if (!frustum.frustum.containsSphere(p.position, p.clipRadius)) {
						p.drawClouds(params, cam.combined, p.getTransform(mat));
						if (p.hasGrid() && p == params.planet && params.drawUi) {
							Vars.renderer.planets.renderSectors(p, params);
						}

						if (cam.frustum.containsSphere(p.position, p.clipRadius) && p.parent != null && p.hasAtmosphere && (params.alwaysDrawAtmosphere || Core.settings.getBool("atmosphere"))) {
							p.drawAtmosphere(Vars.renderer.planets.atmosphere, cam);
						}
					}
				}

				Vars.renderer.planets.batch.proj(cam.combined);

				orbit.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
				orbit.begin(Color.clear);
				Blending.disabled.apply();

				for (Planet p : requests) {
					if (!p.visible()) continue;
					if (params.drawUi) Vars.renderer.planets.renderOrbit(p, params);
				}

				Blending.normal.apply();
				orbit.end();

				var stencil = HShaders.blackHoleStencil;
				stencil.camera = cam;
				stencil.planet = this;
				Draw.blit(stencil);
			});
			pov.end();
		});

		cam.up.set(up);
		cam.direction.set(dir);
		cam.fov = fov;
		cam.width = w;
		cam.height = h;
		cam.update();

		shader.camera = cam;
		shader.planet = this;
		Draw.blit(shader);

		for (Planet child : stashChildren) Vars.renderer.planets.renderPlanet(child, params);
		for (Planet child : stashChildren) Vars.renderer.planets.renderTransparent(child, params);
	}

	@Override
	public void drawClouds(PlanetParams params, Mat3D projection, Mat3D transform) {
		// Do nothing, it's already handled in `draw()`.
	}

	@Override
	public void drawAtmosphere(Mesh atmosphere, Camera3D cam) {
		// The same.
	}

	protected void visit(Planet planet, Cons<Planet> cons) {
		for (Planet child : (planet == this && drawing) ? stashChildren : planet.children) {
			cons.get(child);
			visit(child, cons);
		}
	}
}
