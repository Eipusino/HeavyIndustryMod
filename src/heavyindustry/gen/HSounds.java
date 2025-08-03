package heavyindustry.gen;

import arc.audio.Sound;
import arc.files.Fi;
import heavyindustry.HVars;
import mindustry.Vars;

public final class HSounds {
	public static Sound ct1 = new Sound();
	public static Sound dbz1 = new Sound();
	public static Sound dd1 = new Sound();
	public static Sound fj = new Sound();
	public static Sound jg1 = new Sound();
	public static Sound flak = new Sound();
	public static Sound flak2 = new Sound();
	public static Sound shock = new Sound();
	public static Sound jumpIn = new Sound();
	public static Sound launch = new Sound();
	public static Sound gauss = new Sound();
	public static Sound radar = new Sound();
	public static Sound fissure = new Sound();
	public static Sound blaster = new Sound();
	public static Sound hugeShoot = new Sound();
	public static Sound hugeBlast = new Sound();
	public static Sound largeBeam = new Sound();
	public static Sound metalPipe = new Sound();
	public static Sound metalWalk = new Sound();
	public static Sound alert2 = new Sound();
	public static Sound hammer = new Sound();
	public static Sound coolingFan = new Sound();
	public static Sound hailRain = new Sound();
	public static Sound bigHailstoneHit = new Sound();
	public static Sound giantHailstoneHit = new Sound();
	public static Sound railGunBlast = new Sound();
	public static Sound railGunCharge = new Sound();
	public static Sound shootAltHeavy = new Sound();
	public static Sound shootAltLight = new Sound();
	public static Sound flowrateAbosrb = new Sound();
	public static Sound eviscerationCharge = new Sound();
	public static Sound eviscerationBlast = new Sound();

	/** Don't let anyone instantiate this class. */
	private HSounds() {}

	public static void load() {
		ct1 = new Sound(ogg("ct1"));
		dbz1 = new Sound(ogg("dbz1"));
		dd1 = new Sound(ogg("dd1"));
		fj = new Sound(ogg("fj"));
		jg1 = new Sound(ogg("jg1"));
		flak = new Sound(mp3("flak"));
		flak2 = new Sound(ogg("flak2"));
		launch = new Sound(ogg("launch"));
		shock = new Sound(ogg("shock"));
		jumpIn = new Sound(ogg("jumpIn"));
		gauss = new Sound(ogg("gauss"));
		radar = new Sound(mp3("radar"));
		fissure = new Sound(ogg("fissure"));
		blaster = new Sound(ogg("blaster"));
		hugeShoot = new Sound(ogg("hugeShoot"));
		hugeBlast = new Sound(ogg("hugeBlast"));
		largeBeam = new Sound(ogg("largeBeam"));
		metalPipe = new Sound(ogg("metalPipe"));
		metalWalk = new Sound(ogg("metalWalk"));
		alert2 = new Sound(ogg("alert2"));
		hammer = new Sound(ogg("hammer"));
		coolingFan = new Sound(ogg("coolingFan"));
		hailRain = new Sound(ogg("hailRain"));
		bigHailstoneHit = new Sound(ogg("bigHailstoneHit"));
		giantHailstoneHit = new Sound(ogg("giantHailstoneHit"));
		railGunBlast = new Sound(ogg("railGunBlast"));
		railGunCharge = new Sound(ogg("railGunCharge"));
		shootAltHeavy = new Sound(ogg("shootAltHeavy"));
		shootAltLight = new Sound(ogg("shootAltLight"));
		flowrateAbosrb = new Sound(ogg("flowrateAbsorb"));
		eviscerationCharge = new Sound(ogg("eviscerationCharge"));
		eviscerationBlast = new Sound(ogg("eviscerationBlast"));
	}

	public static void alertLoop() {
		if (!Vars.headless) {
			Vars.control.sound.loop(alert2, 2f);
		}
	}

	public static Fi ogg(String name) {
		return HVars.internalTree.child("sounds/" + name + ".ogg");
	}

	public static Fi mp3(String name) {
		return HVars.internalTree.child("sounds/" + name + ".mp3");
	}
}
