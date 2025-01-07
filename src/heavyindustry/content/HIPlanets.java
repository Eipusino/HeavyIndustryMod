package heavyindustry.content;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.core.*;
import heavyindustry.graphics.g3d.*;
import heavyindustry.maps.ColorPass.*;
import heavyindustry.maps.*;
import heavyindustry.maps.HeightPass.*;
import heavyindustry.maps.planets.*;
import heavyindustry.type.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.content.Planets.*;

/** Defines the {@linkplain Planet planets} and other celestial objects this mod offers. */
public final class HIPlanets {
    public static Planet kepler, gliese;

    /** HIPlanets should not be instantiated. */
    private HIPlanets() {}

    /**
     * Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}.
     * <p>Remember not to execute it a second time, I did not take any precautionary measures.
     */
    public static void load() {
        kepler = new BetterPlanet("kepler", sun, 1f, 3) {{
            orbitRadius = 40f;
            atmosphereRadIn = 0f;
            atmosphereRadOut = 0.3f;
            atmosphereColor = Blocks.water.mapColor;

            Vec3 ringPos = new Vec3(0, 1, 0).rotate(Vec3.X, 25);

            generator = new KeplerPlanetGenerator() {{
                baseHeight = 0;
                baseColor = Blocks.stone.mapColor;

                Mathf.rand.setSeed(2);
                heights.add(new NoiseHeight() {{
                    offset.set(1000, 0, 0);
                    octaves = 7;
                    persistence = 0.5;
                    magnitude = 1;
                    heightOffset = -0.5f;
                }});
                Seq<HeightPass> mountains = new Seq<>();
                for (int i = 0; i < 30; i++) {
                    mountains.add(new DotHeight() {{
                        dir.setToRandomDirection().y *= 10f;
                        dir.rotate(Vec3.X, 22f);
                        min = 0.99f;
                        magnitude = Math.abs(Tmp.v31.set(dir).nor().rotate(Vec3.X, -22f).y) * Mathf.random(0.5f);
                        interp = Interp.exp10In;
                    }});
                }
                heights.add(new MultiHeight(mountains, MultiHeight.MixType.max, MultiHeight.Operation.add), new ClampHeight(0f, 0.8f));
                colors.addAll(
                        new NoiseColorPass() {{
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 3;
                            magnitude = 1.2f;
                            min = 0.3f;
                            max = 0.6f;
                            out = HIBlocks.corruptedMoss.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new NoiseColorPass() {{
                            seed = 5;
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 5;
                            magnitude = 1.2f;
                            min = 0.1f;
                            max = 0.4f;
                            out = HIBlocks.overgrownGrass.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new NoiseColorPass() {{
                            seed = 8;
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 7;
                            magnitude = 1.2f;
                            min = 0.1f;
                            max = 0.4f;
                            out = HIBlocks.mycelium.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }}
                );
                for (int i = 0; i < 5; i++) {
                    colors.add(new SphereColorPass(new Vec3().setToRandomDirection(), 0.06f, Blocks.darksand.mapColor));
                }
                colors.add(
                        new FlatColorPass() {{
                            min = max = 0f;
                            out = Blocks.water.mapColor;
                        }},
                        new FlatColorPass() {{
                            min = 0.3f;
                            max = 0.5f;
                            out = Blocks.snow.mapColor;
                        }},
                        new FlatColorPass() {{
                            max = 1f;
                            min = 0.5f;
                            out = Blocks.iceSnow.mapColor;
                        }}
                );
            }};
            sectorSeed = 3;
            defaultEnv = Env.terrestrial | Env.groundOil | Env.groundWater | Env.oxygen;
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = false;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };
            alwaysUnlocked = true;
            meshLoader = () -> new MultiMesh(
                    new AtmosphereHexMesh(7),
                    new HexMesh(this, 7),

                    new CircleMesh(circle("ring4.png"), this, 80, 2.55f, 2.6f, ringPos),
                    new CircleMesh(circle("ring3.png"), this, 80, 2.2f, 2.5f, ringPos),
                    new CircleMesh(circle("ring2.png"), this, 80, 1.9f, 2.1f, ringPos),
                    new CircleMesh(circle("ring1.png"), this, 80, 1.8f, 1.85f, ringPos)
            );
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 6, -0.5f, 0.14f, 6, Blocks.water.mapColor.cpy().a(0.2f), 2, 0.42f, 1f, 0.6f),
                    new HexSkyMesh(this, 1, 0.6f, 0.15f, 6, Blocks.water.mapColor.cpy().a(0.2f), 2, 0.42f, 1.2f, 0.5f)
            );
        }
            Texture circle(String name) {
                return new Texture(HeavyIndustryMod.internalTree.child("sprites/planets/kepler/rings/" + name));
            }
        };
        gliese = new BetterPlanet("gliese", sun, 1f, 3) {{
            Vec3 ringPos = new Vec3(0,1,0).rotate(Vec3.X, 35);

            generator = new GliesePlanetGenerator() {{
                baseHeight = 0f;
                baseColor = Color.valueOf("212630");
                heights.addAll(
                        new NoiseHeight() {{
                            seed = 6;
                            persistence = 0.62f;
                            octaves = 4;
                            scale = 1.2;
                            magnitude = 1.35f;
                            heightOffset = -0.7f;
                            offset.set(500f, 0f, -500f);
                        }}
                );
                heights.add(new ClampHeight(0f, 0.85f));
                Mathf.rand.setSeed(10);
                Seq<HeightPass> mountains = new Seq<>();
                for (int i = 0; i < 3; i++) {
                    mountains.add(new DotHeight() {{
                        dir.setToRandomDirection().y = Mathf.random(5f, 1f);
                        min = -1f;
                        magnitude = 0.06f;
                        interp = Interp.exp10In;
                    }});
                }
                heights.add(new MultiHeight(mountains, MultiHeight.MixType.max, MultiHeight.Operation.add));
                heights.add(new ClampHeight(0f, 0.95f));
                colors.addAll(
                        new NoiseColorPass() {{
                            seed = 7;
                            scale = 1.4;
                            persistence = 1;
                            octaves = 3;
                            magnitude = 1.2f;
                            min = 0f;
                            max = 0.55f;
                            out = Color.valueOf("8d9ac3");
                            offset.set(1500f, 0f, 0f);
                        }},
                        new NoiseColorPass() {{
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 3;
                            magnitude = 1.2f;
                            min = 0f;
                            max = 0.6f;
                            out = HIBlocks.stoneHalf.mapColor;
                            offset.set(1500f, 0f, 0f);
                        }},
                        new NoiseColorPass() {{
                            seed = 5;
                            scale = 1.5;
                            persistence = 0.2;
                            octaves = 1;
                            magnitude = 1.2f;
                            min = 0f;
                            max = 0.35f;
                            out = HIBlocks.stoneFull.mapColor;
                            offset.set(1500f, 0f, 0f);
                        }},
                        new NoiseColorPass() {{
                            seed = 9;
                            scale = 1.5;
                            persistence = 0.8f;
                            octaves = 9;
                            magnitude = 1f;
                            min = 0f;
                            max = 0.4f;
                            out = HIBlocks.stoneFullTiles.mapColor;
                            offset.set(1500f, 0f, 0f);
                        }},
                        new NoiseColorPass() {{
                            seed = 8;
                            scale = 4.5;
                            persistence = 1;
                            octaves = 2;
                            magnitude = 6f;
                            min = 0f;
                            max = 0.4f;
                            out = HIBlocks.stoneTiles.mapColor;
                            offset.set(1500f, 0f, 0f);
                        }},
                        new FlatColorPass() {{
                            max = 1f;
                            min = 0.52f;
                            out = Color.valueOf("99adc9");
                        }},
                        new FlatColorPass() {{
                            min = 0;
                            max = 0.02f;
                            out = Blocks.stone.mapColor;
                        }}
                );
            }};
            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, 7),

                    new CircleMesh(circle("ring1.png"), this, 80, 2.55f, 2.6f, ringPos),
                    new CircleMesh(circle("ring3.png"), this,80, 2.2f, 2.5f, ringPos),
                    new CircleMesh(circle("ring3.png"), this,80, 1.9f, 2.1f, ringPos)
            );
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 6, new Color().set(Color.gray).mul(0.9f).a(0.55f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 6, Color.white.cpy().lerp(Color.gray, 0.55f).a(0.25f), 2, 0.45f, 1f, 0.61f)
            );
            launchCapacityMultiplier = 0.5f;
            sectorSeed = 2;
            orbitRadius = 80;
            orbitSpacing = 30;
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = true;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };
            iconColor = Color.valueOf("0044ff");
            atmosphereColor = Color.valueOf("4d4372");
            atmosphereRadIn = -0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 15;
            alwaysUnlocked = true;
            landCloudColor = Color.blue.cpy().a(0.5f);
        }
            Texture circle(String name) {
                return new Texture(HeavyIndustryMod.internalTree.child("sprites/planets/gliese/rings/" + name));
            }
        };
    }
}
