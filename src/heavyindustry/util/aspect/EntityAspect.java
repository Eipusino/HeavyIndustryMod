package heavyindustry.util.aspect;

import arc.Events;
import arc.func.Boolf;
import arc.struct.Seq;
import dynamilize.DynamicClass;
import dynamilize.runtimeannos.AspectInterface;
import heavyindustry.HVars;
import heavyindustry.util.handler.ObjectHandler;
import mindustry.entities.EntityGroup;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Fire;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Puddle;
import mindustry.gen.Syncc;
import mindustry.gen.Unit;
import mindustry.gen.WeatherState;
import mindustry.gen.WorldLabel;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class EntityAspect<E extends Entityc> extends AbstractAspect<E, EntityGroup<E>> {
	private final Group group;
	private final Boolf<E> filter;

	public EntityAspect(Group group, Boolf<E> filter) {
		super((EntityGroup<E>) group.group);
		this.group = group;
		this.filter = filter;
		group.addAspect((EntityAspect<Entityc>) this);
	}

	@Override
	public EntityGroup<E> instance() {
		return group.aspectGroup;
	}

	@Override
	public boolean filter(E target) {
		return filter.get(target);
	}

	@Override
	public void releaseAspect() {
		super.releaseAspect();
		group.removeAspect((EntityAspect<Entityc>) this);
	}

	@SuppressWarnings("rawtypes")
	public enum Group {
		all(Entityc.class),
		player(Player.class),
		bullet(Bullet.class),
		unit(Unit.class),
		build(Building.class),
		sync(Syncc.class),
		draw(Drawc.class),
		fire(Fire.class),
		puddle(Puddle.class),
		weather(WeatherState.class),
		label(WorldLabel.class);

		static {
			Events.on(EventType.ResetEvent.class, e -> Group.reset());
		}

		private final Class<?> type;

		private final Field field;
		private final Seq<EntityAspect<Entityc>> aspects = new Seq<>();
		private DynamicClass GroupAspectType;
		private EntityGroup aspectGroup;
		private EntityGroup group;

		Group(Class<?> type) {
			this.type = type;
			try {
				field = Groups.class.getField(name());
				setAspect();
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}

		public static void rebuildAll() {
			for (Group group : values()) {
				group.rebuild();
			}
		}

		public static void reset() {
			for (Group group : values()) {
				for (EntityAspect<Entityc> aspect : group.aspects) {
					aspect.reset();
				}
			}
		}

		public void rebuild() {
			group = null;
			GroupAspectType.delete();
			aspects.clear();
			aspectGroup = null;

			setAspect();
		}

		public void makeAspectType() {
			if (GroupAspectType != null) return;

			GroupAspectType = DynamicClass.get("GroupAspectType$" + name());

			GroupAspectType.setFunction(
					"add",
					(self, supe, args) -> {
						supe.invokeFunc("add", args);
						for (EntityAspect<Entityc> aspect : aspects) {
							aspect.add(args.get(0));
						}
					},
					Entityc.class
			);

			GroupAspectType.setFunction(
					"remove",
					(self, supe, args) -> {
						supe.invokeFunc("remove", args);
						for (EntityAspect<Entityc> aspect : aspects) {
							aspect.remove(args.<Entityc>get(0));
						}
					},
					Entityc.class
			);

			GroupAspectType.setFunction(
					"removeByID",
					(self, sup, args) -> {
						Entityc e = self.<EntityGroup>objSelf().getByID(args.get(0));
						sup.invokeFunc("removeByID", args);
						for (EntityAspect<Entityc> aspect : aspects) {
							aspect.remove(e);
						}
					},
					int.class
			);

			GroupAspectType.setFunction(
					"removeIndex",
					(self, sup, args) -> {
						sup.invokeFunc("removeIndex", args);
						for (EntityAspect<Entityc> aspect : aspects) {
							aspect.remove(args.<Entityc>get(0));
						}
					},
					Entityc.class, int.class
			);
		}

		private void setAspect() {
			try {
				EntityGroup<? extends Entityc> group = (EntityGroup<?>) field.get(null);
				if (this.group == group) return;

				this.group = group;
				makeAspectType();
				aspectGroup = HVars.classHandler.getDynamicMaker().newInstance(
						group.getClass(),
						new Class[]{GroupAsp.class},
						GroupAspectType,
						type,
						false,
						false
				).objSelf();

				ObjectHandler.copyField(group, aspectGroup);

				field.set(null, aspectGroup);
				for (Entityc e : group) {
					for (EntityAspect<Entityc> aspect : aspects) {
						aspect.add(e);
					}
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public void addAspect(EntityAspect<Entityc> aspect) {
			aspects.add(aspect);
		}

		public void removeAspect(EntityAspect<Entityc> aspect) {
			aspects.remove(aspect);
		}
	}

	@AspectInterface
	public interface GroupAsp {
		void add(Entityc e);

		void remove(Entityc e);

		void removeByID(int id);

		void removeIndex(Entityc e, int index);
	}
}
