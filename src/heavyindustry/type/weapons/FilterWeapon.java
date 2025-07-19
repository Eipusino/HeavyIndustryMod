package heavyindustry.type.weapons;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Strings;
import mindustry.content.Bullets;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

/** a weapon that shoots different things depending on things in a unit. */
public class FilterWeapon extends Weapon {
	/** separate from bulletFilter for stats. */
	public BulletType[] bullets = new BulletType[]{Bullets.placeholder};
	public Func<Unit, BulletType> bulletFilter = unit -> bullets[0];
	// TODO i don't know how to make those icons work, and i have no clue as to why it is casting a string to BulletType
	public TextureRegion[] iconRegions;
	public String[] icons = new String[]{""};

	public FilterWeapon(String name) {
		super(name);
	}

	public FilterWeapon() {
		this("");
	}

	@Override
	public void addStats(UnitType u, Table t) {
		if (inaccuracy > 0) {
			t.row();
			t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int) inaccuracy + " " + StatUnit.degrees.localized());
		}
		if (!alwaysContinuous && reload > 0) {
			t.row();
			t.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / reload * shoot.shots, 2) + " " + StatUnit.perSecond.localized());
		}

		t.row();
		t.table(Styles.grayPanel, weapon -> {
			for (int i = 0; i < bullets.length; i++) {
				int j = i;
				weapon.table(Tex.underline, b -> {
					b.left();
					if (iconRegions[j].found()) b.image(iconRegions[j]).padRight(10).center();
					StatValues.ammo(ObjectMap.of(u, bullets[j])).display(b.add(new Table()).get());
				}).growX().row();
			}
		}).margin(10f);
	}

	@Override
	public void load() {
		super.load();
		iconRegions = new TextureRegion[bullets.length];
		for (int i = 0; i < iconRegions.length; i++) {
			if (i < icons.length) {
				iconRegions[i] = Core.atlas.find(icons[i]);
			} else {
				iconRegions[i] = Core.atlas.find("error");
			}
		}
	}

	@Override
	protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
		bullet = bulletFilter.get(unit);
		super.shoot(unit, mount, shootX, shootY, rotation);
	}
}
