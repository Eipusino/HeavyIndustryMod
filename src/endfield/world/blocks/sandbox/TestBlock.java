package endfield.world.blocks.sandbox;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import endfield.util.Reflects;
import endfield.world.blocks.IBlock;
import endfield.world.blocks.IBuilding;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class TestBlock extends Block implements IBlock {
	public MethodHandle buildCons;

	public TestBlock(String name) {
		super(name);

		envEnabled = Env.any;

		solid = true;
		destructible = true;
		targetable = false;
	}

	@Override
	protected void initBuilding() {
		//attempt to find the first declared class and use it as the entity type
		try {
			Class<?> current = getClass();

			if (current.isAnonymousClass()) {
				current = current.getSuperclass();
			}

			subclass = current;

			while (buildType == null && Block.class.isAssignableFrom(current)) {
				//first class that is subclass of Building
				Class<?>[] types = current.getDeclaredClasses();
				for (Class<?> type : types) {
					if (Building.class.isAssignableFrom(type) && !type.isInterface()) {
						//these are inner classes, so they have an implicit parameter generated
						buildCons = Reflects.publicLookup.findConstructor(type, MethodType.methodType(void.class, type.getDeclaringClass()));

						buildType = () -> {
							try {
								return (Building) buildCons.invoke(this);
							} catch (Throwable e) {
								return new TestBuild();
							}
						};
					}
				}

				//scan through every superclass looking for it
				current = current.getSuperclass();
			}

		} catch (Throwable e) {
			Log.err(e);
		}

		if (buildType == null) {
			//assign default value
			buildType = TestBuild::new;
		}
	}

	@Override
	public int size() {
		return size;
	}

	public class TestBuild extends Building implements IBuilding {
		@Override
		public void buildConfiguration(Table table) {
		}

		@Override
		public Block block() {
			return TestBlock.this;
		}

		@Override
		public Tile tile() {
			return tile;
		}

		@Override
		public float efficiency() {
			return efficiency;
		}

		@Override
		public Seq<Building> proximity() {
			return proximity;
		}
	}
}
